package game.controller;

import game.engine.Game;
import game.engine.cells.Cell;
import game.engine.cells.DoorCell;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;
import game.view.GameView;
import game.view.ViewManager;
import game.view.WinView;

/**
 * CONTROLLER — wires the Game engine to the GameView.
 *
 * Responsibilities:
 *  - Attach action handlers to the Roll and Power buttons.
 *  - After each engine call, pull new state and push it to the View.
 *  - Never contain any game rules; never build any UI widgets.
 */
public class GameController {

    private final Game     game;
    private final GameView gameView;

    public GameController(Game game, GameView gameView) {
        this.game     = game;
        this.gameView = gameView;

        initializeView();
        setupHandlers();
    }

    // ── Initial paint ────────────────────────────────────────────────

    private void initializeView() {
        // Place tokens at cell 0
        gameView.movePlayer(game.getPlayer()  .getPosition(), true);
        gameView.movePlayer(game.getOpponent().getPosition(), false);

        // Label every door cell with its energy value
        for (int i = 1; i < 100; i += 2) {          // doors are at odd indices
            Cell cell = game.getBoard().getCell(i);
            if (cell instanceof DoorCell) {
                DoorCell door = (DoorCell) cell;
                gameView.setDoorEnergyLabel(i, door.getEnergy());
            }
        }

        refreshStats();
        updateTurnLog();
    }

    // ── Button handlers ──────────────────────────────────────────────

    private void setupHandlers() {
        gameView.getRollBtn() .setOnAction(e -> handleRoll());
        gameView.getPowerBtn().setOnAction(e -> handlePower());
    }

    private void handleRoll() {
        Monster current   = game.getCurrent();
        boolean wasFrozen = current.isFrozen();

        try {
            game.playTurn();                  // may throw InvalidMoveException
        } catch (InvalidMoveException ex) {
            gameView.showAlert(
                "Cell Occupied!",
                "That cell is occupied by your opponent.\nTry rolling again."
            );
            return;
        }

        // Always sync positions and stats after a successful turn
        gameView.movePlayer(game.getPlayer()  .getPosition(), true);
        gameView.movePlayer(game.getOpponent().getPosition(), false);
        refreshStats();

        if (wasFrozen) {
            gameView.updateLog("❄  " + current.getName() + " was FROZEN — turn skipped!");
        } else {
            gameView.showDiceResult(game.getLastRoll());
        }

        // Check exhausted doors and grey them out
        for (int i = 1; i < 100; i += 2) {
            Cell cell = game.getBoard().getCell(i);
            if (cell instanceof DoorCell && ((DoorCell) cell).isActivated()) {
                gameView.markDoorExhausted(i);
            }
        }

        // Check win condition
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

    // ── Helpers ──────────────────────────────────────────────────────

    private void refreshStats() {
        Monster p = game.getPlayer();
        Monster o = game.getOpponent();

        gameView.updateMonsterCard(
            true,
            p.getName(),
            p.getOriginalRole().toString(),
            p.getRole().toString(),
            p.getClass().getSimpleName(),
            p.getEnergy(),
            p.getPosition(),
            buildStatus(p)
        );
        gameView.updateMonsterCard(
            false,
            o.getName(),
            o.getOriginalRole().toString(),
            o.getRole().toString(),
            o.getClass().getSimpleName(),
            o.getEnergy(),
            o.getPosition(),
            buildStatus(o)
        );
    }

    private String buildStatus(Monster m) {
        StringBuilder sb = new StringBuilder();
        if (m.isShielded())  sb.append("🛡 Shield  ");
        if (m.isConfused())  sb.append("😵 Confused(").append(m.getConfusionTurns()).append(")  ");
        if (m.isFrozen())    sb.append("❄ Frozen  ");
        if (sb.length() == 0) sb.append("✅ Normal");
        return sb.toString().trim();
    }

    private void updateTurnLog() {
        Monster current = game.getCurrent();
        boolean isPlayerTurn = current == game.getPlayer();
        String prefix = isPlayerTurn ? "🎯 YOUR TURN" : "⚔  OPPONENT'S TURN";
        gameView.updateLog(prefix + " — " + current.getName());
    }

    private void navigateToWin(Monster winner) {
        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();
        Monster loser    = (winner == player) ? opponent : player;

        WinView winView = new WinView(
            winner.getName(), winner.getRole().toString(), winner.getEnergy(),
            loser.getName(),  loser.getEnergy()
        );
        ViewManager.updateView(winView);
    }
}
