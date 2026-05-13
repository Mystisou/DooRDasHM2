package game.gui.controller;

import game.engine.Game;
import game.engine.cells.Cell;
import game.engine.cells.DoorCell;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;
import game.gui.view.GameView;
import game.gui.view.ViewManager;
import game.gui.view.WinView;

public class GameController {

    private final Game     game;
    private final GameView gameView;

    public GameController(Game game, GameView gameView) {
        this.game     = game;
        this.gameView = gameView;
        initializeView();
        setupHandlers();
    }

    private void initializeView() {
        gameView.movePlayer(game.getPlayer()  .getPosition(), true);
        gameView.movePlayer(game.getOpponent().getPosition(), false);

        gameView.setPlayerPhoto(true,  game.getPlayer()  .getName(), game.getPlayer()  .getOriginalRole().toString());
        gameView.setPlayerPhoto(false, game.getOpponent().getName(), game.getOpponent().getOriginalRole().toString());

        for (int i = 1; i < 100; i += 2) {
            Cell cell = game.getBoard().getCell(i);
            if (cell instanceof DoorCell) {
                gameView.setDoorEnergyLabel(i, ((DoorCell) cell).getEnergy());
            }
        }

        refreshStats();
        updateTurnLog();
    }

    private void setupHandlers() {
        gameView.getDiceImageView().setOnMouseClicked(e -> handleRoll());
        gameView.getPowerBtn().setOnAction(e -> handlePower());
    }

    private void handleRoll() {
        Monster current   = game.getCurrent();
        boolean wasFrozen = current.isFrozen();

        try {
            game.playTurn();
        } catch (InvalidMoveException ex) {
            gameView.showAlert(
                "Cell Occupied!",
                "That cell is occupied by your opponent.\nTry rolling again."
            );
            return;
        }

        gameView.movePlayer(game.getPlayer()  .getPosition(), true);
        gameView.movePlayer(game.getOpponent().getPosition(), false);
        refreshStats();

        if (wasFrozen) {
            gameView.updateLog("❄  " + current.getName() + " was FROZEN — turn skipped!");
        } else {
            gameView.showDiceResult(game.getLastRoll());
        }

        for (int i = 1; i < 100; i += 2) {
            Cell cell = game.getBoard().getCell(i);
            if (cell instanceof DoorCell && ((DoorCell) cell).isActivated()) {
                gameView.markDoorExhausted(i);
            }
        }

        Monster winner = game.getWinner();
        if (winner != null) {
            navigateToWin(winner);
            return;
        }

        updateTurnLog();
    }

    private void handlePower() {
        String name = game.getCurrent().getName();
        try {
            game.usePowerup();
            refreshStats();
            gameView.updateLog("⚡  " + name + " activated their power! Now roll the dice.");
        } catch (OutOfEnergyException ex) {
            gameView.showAlert(
                "Not Enough Energy",
                "You need at least 500 energy to activate your power-up."
            );
        }
    }

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
        if (sb.length() == 0) sb.append("✅ Normal");
        return sb.toString().trim();
    }

    private void updateTurnLog() {
        boolean isPlayerTurn = game.getCurrent() == game.getPlayer();
        gameView.updateLog(isPlayerTurn ? "⚔   YOUR TURN!" : "🤖   OPPONENT'S TURN!");
        gameView.setDiceTurnIndicator(isPlayerTurn);
    }

    private void navigateToWin(Monster winner) {
        Monster player  = game.getPlayer();
        Monster opponent = game.getOpponent();
        Monster loser   = (winner == player) ? opponent : player;

        ViewManager.updateView(new WinView(
            winner.getName(), winner.getRole().toString(), winner.getEnergy(),
            loser.getName(),  loser.getEnergy()
        ));
    }
}
