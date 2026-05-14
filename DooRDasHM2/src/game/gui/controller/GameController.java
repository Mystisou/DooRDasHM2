package game.gui.controller;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.cards.Card;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.exceptions.GameActionException;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Dasher;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import game.gui.view.GameView;
import game.gui.view.ViewManager;
import game.gui.view.WinView;
import game.gui.view.popups.CardPopup;

import java.util.ArrayList;

public class GameController {

    private final Game     game;
    private final GameView gameView;

    /** Tracks which door cells have already been visually marked as activated,
     *  so markDoorActivated() is called at most once per door. */
    private final boolean[] activatedDoors = new boolean[100];

    public GameController(Game game, GameView gameView) {
        this.game     = game;
        this.gameView = gameView;
        initializeView();
        setupHandlers();
    }

    // ── initialisation ───────────────────────────────────────────────────────

    private void initializeView() {
        gameView.movePlayer(game.getPlayer()  .getPosition(), true);
        gameView.movePlayer(game.getOpponent().getPosition(), false);

        gameView.setPlayerPhoto(true,  game.getPlayer()  .getName(), game.getPlayer()  .getOriginalRole().toString());
        gameView.setPlayerPhoto(false, game.getOpponent().getName(), game.getOpponent().getOriginalRole().toString());

        // door energy labels
        for (int i = 1; i < 100; i += 2) {
            Cell cell = game.getBoard().getCell(i);
            if (cell instanceof DoorCell)
                gameView.setDoorEnergyLabel(i, ((DoorCell) cell).getEnergy());
        }

        // monster cell name badges
        for (int idx : Constants.MONSTER_CELL_INDICES) {
            Cell cell = game.getBoard().getCell(idx);
            if (cell instanceof MonsterCell) {
                Monster stationed = ((MonsterCell) cell).getCellMonster();
                if (stationed != null)
                    gameView.addMonsterNameLabel(idx, abbrev(stationed.getName()));
            }
        }

        refreshStats();
        gameView.updateCardPileCount(Board.cards.size());
        setTurnState("⚔   YOUR TURN!", true);
    }

    private void setupHandlers() {
        gameView.getDiceImageView().setOnMouseClicked(e -> handleRoll());
        gameView.getPowerBtn().setOnAction(e -> handlePower());
    }

    // ── turn handling ────────────────────────────────────────────────────────

    private void handleRoll() {
        Monster current  = game.getCurrent();
        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();
        boolean wasFrozen = current.isFrozen();
        boolean isPlayer  = (current == player);

        // ── pre-turn snapshots ────────────────────────────────────────────────
        ArrayList<Card> deckSnapshot    = new ArrayList<>(Board.cards);
        int playerEnergyBefore          = player.getEnergy();
        int opponentEnergyBefore        = opponent.getEnergy();
        boolean wasPlayerShielded       = player.isShielded();
        boolean wasOpponentShielded     = opponent.isShielded();
        int posBefore                   = current.getPosition();
        int momentumBefore              = (current instanceof Dasher)
                                            ? ((Dasher) current).getMomentumTurns() : 0;
        int normalSpeedBefore           = (current instanceof MultiTasker)
                                            ? ((MultiTasker) current).getNormalSpeedTurns() : 0;

        // ── run the turn ──────────────────────────────────────────────────────
        try {
            game.playTurn();
        } catch (InvalidMoveException ex) {
            gameView.showAlert(
                "Cell Occupied!",
                "That cell is occupied by your opponent.\nTry rolling again."
            );
            return;
        } catch (GameActionException ex) {
            gameView.showAlert(
                "Game Error",
                "An unexpected error occurred:\n" + ex.getMessage() +
                "\nThe game will continue."
            );
            return;
        }

        int roll = game.getLastRoll();

        // ── board / token updates ─────────────────────────────────────────────
        gameView.movePlayer(player  .getPosition(), true);
        gameView.movePlayer(opponent.getPosition(), false);
        refreshStats();   // clears old delta labels

        if (!wasFrozen) gameView.showDiceResult(roll);   // bottom diceLabel only

        // update card pile counter after every turn (drawCard may have fired)
        gameView.updateCardPileCount(Board.cards.size());

        // door activation visuals — only call markDoorActivated once per door
        for (int i = 1; i < 100; i += 2) {
            if (!activatedDoors[i]) {
                Cell cell = game.getBoard().getCell(i);
                if (cell instanceof DoorCell && ((DoorCell) cell).isActivated()) {
                    gameView.markDoorActivated(i);
                    activatedDoors[i] = true;
                }
            }
        }

        // ── deltas & event description ────────────────────────────────────────
        // Everything that happened this turn gets distilled into ONE string that
        // is shown in the top logLabel together with the next-turn indicator.
        // This avoids the bug where updateLog() calls overwrite each other.
        String eventMsg;

        if (wasFrozen) {
            eventMsg = "❄  " + current.getName() + " was FROZEN — turn skipped!";
            // Show a blocking popup so the freeze cannot be missed
            gameView.showFreezeSkip(current.getName());

        } else {
            int playerDelta   = player.getEnergy()   - playerEnergyBefore;
            int opponentDelta = opponent.getEnergy() - opponentEnergyBefore;

            // ── stat-card delta labels (separate small labels, NOT logLabel) ──
            if (playerDelta   != 0) gameView.showEnergyDelta(true,  playerDelta);
            if (opponentDelta != 0) gameView.showEnergyDelta(false, opponentDelta);

            // ── what cell did the dice actually land on (pre-transport)? ──────
            int effectiveRoll = computeEffectiveRoll(current, roll, momentumBefore, normalSpeedBefore);
            int rolledPos     = (posBefore + effectiveRoll) % Constants.BOARD_SIZE;
            Cell rolledCell   = game.getBoard().getCell(rolledPos);

            Card drawn = detectDrawnCard(deckSnapshot);

            if (rolledCell instanceof ConveyorBelt) {
                eventMsg = "⚙  Conveyor Belt at cell " + rolledPos
                         + " → moved to cell " + current.getPosition() + "!";

            } else if (rolledCell instanceof ContaminationSock) {
                eventMsg = "☠  Contamination Sock at cell " + rolledPos
                         + "! Moved back to cell " + current.getPosition()
                         + "  |  -" + Constants.SLIP_PENALTY + " ⚡";

            } else if (drawn != null) {
                // card cell: show popup, log handles itself below
                eventMsg = "🃏  Drew: " + drawn.getName() + " — " + shortEffect(drawn);

            } else {
                // shield block?
                boolean playerBlocked   = wasPlayerShielded   && !player.isShielded()   && playerDelta   == 0;
                boolean opponentBlocked = wasOpponentShielded && !opponent.isShielded() && opponentDelta == 0;
                if (playerBlocked) {
                    eventMsg = "🛡  " + player.getName() + "'s shield blocked the energy loss!";
                } else if (opponentBlocked) {
                    eventMsg = "🛡  " + opponent.getName() + "'s shield blocked the energy loss!";
                } else {
                    // door or monster cell — surface the energy change clearly
                    int curDelta = isPlayer ? playerDelta : opponentDelta;
                    if (curDelta > 0)      eventMsg = "🚪  +" + curDelta + " energy from door!";
                    else if (curDelta < 0) eventMsg = "🚪  " + curDelta + " energy from door!";
                    else                   eventMsg = "";   // normal cell, no energy change
                }
            }

            // card popup (showAndWait — blocks until dismissed)
            if (drawn != null) {
                CardPopup.show(
                    drawn,
                    isPlayer,
                    gameView.getScene() != null ? gameView.getScene().getWindow() : null
                );
            }
        }

        // ── win check ─────────────────────────────────────────────────────────
        Monster winner = game.getWinner();
        if (winner != null) {
            navigateToWin(winner);
            return;
        }

        // ── combined log: event + next-turn indicator ─────────────────────────
        // Both are written to logLabel in ONE call so nothing overwrites anything.
        boolean isPlayerTurnNext = (game.getCurrent() == player);
        String turnTag = isPlayerTurnNext ? "⚔  YOUR TURN!" : "🤖  OPPONENT'S TURN!";
        String finalLog = eventMsg.isEmpty()
            ? turnTag
            : eventMsg + "    |    " + turnTag;
        setTurnState(finalLog, isPlayerTurnNext);
    }

    // ── power ─────────────────────────────────────────────────────────────────

    private void handlePower() {
        String name = game.getCurrent().getName();
        try {
            game.usePowerup();
            refreshStats();
            // Keep dice indicator state, just update log text
            boolean isPlayerTurn = game.getCurrent() == game.getPlayer();
            setTurnState("⚡  " + name + " activated their power!    |    Roll the dice.", isPlayerTurn);
        } catch (OutOfEnergyException ex) {
            gameView.showAlert(
                "Not Enough Energy",
                "You need at least 500 energy to activate your power-up."
            );
        } catch (GameActionException ex) {
            gameView.showAlert(
                "Power-up Failed",
                "Could not activate power-up:\n" + ex.getMessage()
            );
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /**
     * Sets both the logLabel text and the dice glow in one call.
     * Use this instead of calling updateLog() + setDiceTurnIndicator() separately.
     */
    private void setTurnState(String message, boolean isPlayerTurn) {
        gameView.updateLog(message);
        gameView.setDiceTurnIndicator(isPlayerTurn);
        gameView.resetDiceLabel(isPlayerTurn);          // clear stale roll result for next turn
        gameView.setPowerEnabled(isPlayerTurn);         // power-up only usable on player's own turn
    }

    /**
     * Computes actual cells moved, accounting for Dasher / MultiTasker overrides.
     * Uses pre-turn snapshots of momentum / normalSpeed turns since move() mutates them.
     */
    private int computeEffectiveRoll(Monster m, int roll,
                                     int momentumBefore, int normalSpeedBefore) {
        if (m instanceof Dasher)
            return (momentumBefore > 0) ? roll * 3 : roll * 2;
        if (m instanceof MultiTasker)
            return (normalSpeedBefore > 0) ? roll : roll / 2;
        return roll;
    }

    /**
     * drawCard() always removes index 0 from Board.cards.
     * If the deck shrank by exactly 1, deckSnapshot.get(0) is the drawn card.
     * Returns null if nothing was drawn or the deck reshuffled (rare edge case).
     */
    private Card detectDrawnCard(ArrayList<Card> deckSnapshot) {
        if (!deckSnapshot.isEmpty() && Board.cards.size() == deckSnapshot.size() - 1)
            return deckSnapshot.get(0);
        return null;
    }

    /** First sentence of CardPopup.effectDescription for the compact log line. */
    private String shortEffect(Card card) {
        String full = CardPopup.effectDescription(card);
        int nl  = full.indexOf('\n');
        int dot = full.indexOf('.');
        int cut = -1;
        if (nl  > 0) cut = nl;
        if (dot > 0) cut = (cut < 0) ? dot + 1 : Math.min(cut, dot + 1);
        return (cut > 0 && cut < full.length()) ? full.substring(0, cut).trim() : full;
    }

    /** First name token, max 7 chars — fits the tiny monster-cell badge. */
    private String abbrev(String fullName) {
        String first = fullName.split(" ")[0];
        return first.length() > 7 ? first.substring(0, 6) + "." : first;
    }

    // ── stat refresh ──────────────────────────────────────────────────────────

    private void refreshStats() {
        Monster p = game.getPlayer();
        Monster o = game.getOpponent();
        gameView.updateMonsterCard(
            true,
            p.getName(), p.getOriginalRole().toString(), p.getRole().toString(),
            p.getClass().getSimpleName(), p.getEnergy(), p.getPosition(), buildStatus(p)
        );
        gameView.updateMonsterCard(
            false,
            o.getName(), o.getOriginalRole().toString(), o.getRole().toString(),
            o.getClass().getSimpleName(), o.getEnergy(), o.getPosition(), buildStatus(o)
        );
    }

    private String buildStatus(Monster m) {
        StringBuilder sb = new StringBuilder();
        if (m.isShielded()) sb.append("🛡 Shield  ");
        if (m.isConfused()) sb.append("😵 Confused(").append(m.getConfusionTurns()).append(")  ");
        if (m.isFrozen())   sb.append("❄ Frozen  ");
        if (m instanceof Dasher) {
            int rush = ((Dasher) m).getMomentumTurns();
            if (rush > 0) sb.append("⚡ Rush(").append(rush).append(")  ");
        }
        if (m instanceof MultiTasker) {
            int focus = ((MultiTasker) m).getNormalSpeedTurns();
            if (focus > 0) sb.append("🏃 Focus(").append(focus).append(")  ");
        }
        if (sb.length() == 0) sb.append("✅ Normal");
        return sb.toString().trim();
    }

    private void navigateToWin(Monster winner) {
        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();
        Monster loser    = (winner == player) ? opponent : player;
        ViewManager.updateView(new WinView(
            winner.getName(), winner.getRole().toString(), winner.getEnergy(),
            loser.getName(),  loser.getEnergy()
        ));
    }
}
