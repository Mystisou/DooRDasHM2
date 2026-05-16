package game.gui.controller;

import game.engine.Game;
import game.engine.exceptions.GameActionException;
import game.engine.exceptions.OutOfEnergyException;
import game.gui.view.GameView;

/**
 * Handles the "ACTIVATE POWER" button.
 * Calls game.usePowerup(), catches engine exceptions, and refreshes the view.
 */
public class PowerupController {

    private final Game           game;
    private final GameView       view;
    private final GameController gameCtrl;

    public PowerupController(Game game, GameView view, GameController gameCtrl) {
        this.game     = game;
        this.view     = view;
        this.gameCtrl = gameCtrl;
    }

    public void handlePower() {
        String name = game.getCurrent().getName();
        try {
            game.usePowerup();

            gameCtrl.refreshStats();
            boolean isPlayerTurn = (game.getCurrent() == game.getPlayer());
            gameCtrl.setTurnState(
                name + " activated their power!  |  Roll the dice.",
                isPlayerTurn
            );
        } catch (OutOfEnergyException ex) {
            view.showNotEnoughEnergyPopup();
        } catch (GameActionException ex) {
            view.showAlert("Power-up Failed",
                "Could not activate power-up:\n" + ex.getMessage());
        }
    }
}
