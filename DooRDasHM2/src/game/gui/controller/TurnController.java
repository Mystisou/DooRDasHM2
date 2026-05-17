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

    public void handleRoll() {
        Monster current  = game.getCurrent();
        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();
        boolean wasFrozen = current.isFrozen();
        boolean isPlayer  = (current == player);

        view.resetDiceLabel(isPlayer);

        ArrayList<Card> deckSnapshot    = new ArrayList<>(Board.cards);
        int   playerEnergyBefore        = player  .getEnergy();
        int   opponentEnergyBefore      = opponent.getEnergy();
        boolean wasPlayerShielded       = player  .isShielded();
        boolean wasOpponentShielded     = opponent.isShielded();
        int   posBefore                 = current .getPosition();
        int   momentumBefore            = (current instanceof Dasher)       ? ((Dasher)       current).getMomentumTurns()    : 0;
        int   normalSpeedBefore         = (current instanceof MultiTasker)  ? ((MultiTasker)  current).getNormalSpeedTurns() : 0;

        try {
            game.playTurn();
        } catch (InvalidMoveException ex) {
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

        view.clearEventText();

        final boolean  _isPlayer          = isPlayer;
        final boolean  _wasFrozen          = wasFrozen;
        final int      _playerEnergyBefore  = playerEnergyBefore;
        final int      _opponentEnergyBefore= opponentEnergyBefore;
        final boolean  _wasPlayerShielded   = wasPlayerShielded;
        final boolean  _wasOpponentShielded = wasOpponentShielded;
        final int      _posBefore           = posBefore;
        final int      _momentumBefore      = momentumBefore;
        final int      _normalSpeedBefore   = normalSpeedBefore;
        final ArrayList<Card> _deckSnapshot = deckSnapshot;
        final Monster  _player   = player;
        final Monster  _opponent = opponent;
        final Monster  _current  = current;

        view.movePlayer(player.getPosition(), true,  () ->
        view.movePlayer(opponent.getPosition(), false, () ->
            javafx.application.Platform.runLater(() -> finishTurn(
                roll, _isPlayer, _wasFrozen,
                _playerEnergyBefore, _opponentEnergyBefore,
                _wasPlayerShielded, _wasOpponentShielded,
                _posBefore, _momentumBefore, _normalSpeedBefore,
                _deckSnapshot, _player, _opponent, _current
            ))
        ));
    }

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

        view.updateEventText(eventMsg);

        Monster winner = game.getWinner();
        if (winner != null) {
            if (winner == game.getPlayer()) gameCtrl.navigateToWin(winner);
            else                            gameCtrl.navigateToLoss(winner);
            return;
        }

        boolean isPlayerTurnNext = (game.getCurrent() == player);
        gameCtrl.setTurnState(
            isPlayerTurnNext ? "YOUR TURN!" : "OPPONENT'S TURN!",
            isPlayerTurnNext
        );
    }
}