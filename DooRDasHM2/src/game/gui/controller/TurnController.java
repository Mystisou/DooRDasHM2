package game.gui.controller;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.cards.Card;
import game.engine.exceptions.GameActionException;
import game.engine.exceptions.InvalidMoveException;
import game.engine.monsters.Dasher;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import game.gui.view.GameView;
import game.gui.view.popups.OccupiedCellPopup;

import java.util.ArrayList;

/**
 * Executes one complete player turn.
 * Token animation is async — the rest of the turn continues in the animation callback.
 * Uses OccupiedCellPopup (instead of a plain alert) so the player can re-roll directly.
 */
public class TurnController {

    private final Game           game;
    private final GameView       view;
    private final GameController gameCtrl;
    private final CellController cellCtrl;
    private final CardController cardCtrl;

    public TurnController(Game game, GameView view, GameController gameCtrl,
                          CellController cellCtrl, CardController cardCtrl) {
        this.game     = game;
        this.view     = view;
        this.gameCtrl = gameCtrl;
        this.cellCtrl = cellCtrl;
        this.cardCtrl = cardCtrl;
    }

    // ── entry point ───────────────────────────────────────────────────────────

    public void handleRoll() {
        Monster current  = game.getCurrent();
        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();
        boolean wasFrozen = current.isFrozen();
        boolean isPlayer  = (current == player);

        // Clear the previous roll's number as soon as a new roll starts
        view.resetDiceLabel(isPlayer);

        // pre-turn snapshots
        ArrayList<Card> deckSnapshot    = new ArrayList<>(Board.cards);
        int   playerEnergyBefore        = player  .getEnergy();
        int   opponentEnergyBefore      = opponent.getEnergy();
        boolean wasPlayerShielded       = player  .isShielded();
        boolean wasOpponentShielded     = opponent.isShielded();
        int   posBefore                 = current .getPosition();
        int   momentumBefore            = (current instanceof Dasher)       ? ((Dasher)       current).getMomentumTurns()    : 0;
        int   normalSpeedBefore         = (current instanceof MultiTasker)  ? ((MultiTasker)  current).getNormalSpeedTurns() : 0;

        // execute roll
        try {
            game.playTurn();
        } catch (InvalidMoveException ex) {
            // themed popup — Roll Again button calls handleRoll() recursively
            OccupiedCellPopup.show(
                opponent.getName(),
                view.getScene() != null ? view.getScene().getWindow() : null,
                this::handleRoll
            );
            return;
        } catch (GameActionException ex) {
            view.showAlert("Game Error", ex.getMessage() + "\nThe game will continue.");
            return;
        }

        int roll = game.getLastRoll();

        // ── animate tokens, then finish turn ─────────────────────────────────
        view.clearEventText();

        view.movePlayer(player  .getPosition(), true,  () ->
        view.movePlayer(opponent.getPosition(), false, () ->
            finishTurn(
                roll, isPlayer, wasFrozen,
                playerEnergyBefore, opponentEnergyBefore,
                wasPlayerShielded, wasOpponentShielded,
                posBefore, momentumBefore, normalSpeedBefore,
                deckSnapshot, player, opponent, current
            )
        ));
    }

    // ── post-animation turn finalization ──────────────────────────────────────

    private void finishTurn(
            int roll, boolean isPlayer, boolean wasFrozen,
            int playerEnergyBefore, int opponentEnergyBefore,
            boolean wasPlayerShielded, boolean wasOpponentShielded,
            int posBefore, int momentumBefore, int normalSpeedBefore,
            ArrayList<Card> deckSnapshot,
            Monster player, Monster opponent, Monster current) {

        gameCtrl.refreshStats();
        if (!wasFrozen) view.showDiceResult(roll);
        view.updateCardPileCount(Board.cards.size());
        cellCtrl.updateDoorActivations();

        String eventMsg;

        if (wasFrozen) {
            eventMsg = current.getName() + " was FROZEN — turn skipped!";
            view.showFreezeSkip(current.getName());
        } else {
            int playerDelta   = player  .getEnergy() - playerEnergyBefore;
            int opponentDelta = opponent.getEnergy() - opponentEnergyBefore;
            if (playerDelta   != 0) view.showEnergyDelta(true,  playerDelta);
            if (opponentDelta != 0) view.showEnergyDelta(false, opponentDelta);

            int effectiveRoll = cellCtrl.computeEffectiveRoll(current, roll, momentumBefore, normalSpeedBefore);
            int rolledPos     = (posBefore + effectiveRoll) % Constants.BOARD_SIZE;
            game.engine.cells.Cell rolledCell = game.getBoard().getCell(rolledPos);

            Card drawn = cardCtrl.detectDrawnCard(deckSnapshot);

            eventMsg = cellCtrl.buildEventMessage(
                current, rolledCell, rolledPos, isPlayer,
                playerDelta, opponentDelta,
                wasPlayerShielded, wasOpponentShielded,
                player, opponent, drawn
            );

            cardCtrl.showPopupIfNeeded(drawn, isPlayer,
                view.getScene() != null ? view.getScene().getWindow() : null);
        }

        // show event text in the action panel
        view.updateEventText(eventMsg);

        // win check
        Monster winner = game.getWinner();
        if (winner != null) {
            if (winner == game.getPlayer()) gameCtrl.navigateToWin(winner);
            else                            gameCtrl.navigateToLoss(winner);
            return;
        }

        // end-of-turn — log only shows whose turn it is
        boolean isPlayerTurnNext = (game.getCurrent() == player);
        gameCtrl.setTurnState(
            isPlayerTurnNext ? "YOUR TURN!" : "OPPONENT'S TURN!",
            isPlayerTurnNext
        );
    }
}
