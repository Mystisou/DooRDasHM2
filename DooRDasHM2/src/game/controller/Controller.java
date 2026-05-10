package game.controller;

import game.engine.Game;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.view.GameView;
import game.view.ViewManager;
import game.view.WinView;

public class Controller {
    private Game game;
    private GameView view;

    public Controller(Game game, GameView view) {
        this.game = game;
        this.view = view;
        attachHandlers();
        updateUI();
    }

    private void attachHandlers() {
        view.getRollBtn().setOnAction(e -> handleRoll());
        view.getPowerBtn().setOnAction(e -> handlePowerup());
    }

    private void handleRoll() {
        try {
            game.playTurn();
            updateUI();
            checkWinCondition();
        } catch (InvalidMoveException e) {
            view.showAlert("Invalid Move!", e.getMessage());
        }
    }

    private void handlePowerup() {
        try {
            game.usePowerup();
            updateUI();
        } catch (OutOfEnergyException e) {
            view.showAlert("Powerup Failed", "Not enough energy to use this powerup!");
        }
    }

    private void updateUI() {
        String p1Stats = "Player: " + game.getPlayer().getName() +
                         "\nRole: " + game.getPlayer().getRole() +
                         "\nEnergy: " + game.getPlayer().getEnergy();

        String p2Stats = "Opponent: " + game.getOpponent().getName() +
                         "\nRole: " + game.getOpponent().getRole() +
                         "\nEnergy: " + game.getOpponent().getEnergy();

        view.updateStats(p1Stats, p2Stats);
        view.updateLog("It is now the " + (game.getCurrent() == game.getPlayer() ? "Player's" : "Opponent's") + " turn!");
    }

    private void checkWinCondition() {
        if (game.getWinner() != null) {
            WinView winScreen = new WinView(
                game.getWinner().getName(),
                game.getWinner().getRole().toString(),
                game.getWinner().getEnergy()
            );
            ViewManager.updateView(winScreen);
        }
    }
}
