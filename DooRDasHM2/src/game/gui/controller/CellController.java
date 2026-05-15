package game.gui.controller;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.cards.Card;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.monsters.Dasher;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import game.gui.view.GameView;

/**
 * Handles everything related to the board's cell effects:
 *  - keeps track of which door cells have been activated and updates the view once each
 *  - computes how far the current monster actually moved (accounting for type modifiers)
 *  - builds the human-readable event message shown in the log after each turn
 */
public class CellController {

    private final Game     game;
    private final GameView view;

    /** Guards so markDoorActivated() is called at most once per door. */
    private final boolean[] activatedDoors = new boolean[100];

    public CellController(Game game, GameView view) {
        this.game = game;
        this.view = view;
    }

    // ── door activation ───────────────────────────────────────────────────────

    public void updateDoorActivations() {
        for (int i = 1; i < 100; i += 2) {
            if (!activatedDoors[i]) {
                Cell c = game.getBoard().getCell(i);
                if (c instanceof DoorCell && ((DoorCell) c).isActivated()) {
                    view.markDoorActivated(i);
                    activatedDoors[i] = true;
                }
            }
        }
    }

    // ── movement calculation ──────────────────────────────────────────────────

    /**
     * Returns how many cells the monster actually moved, factoring in
     * Dasher momentum and MultiTasker focus-mode overrides.
     * Uses pre-turn snapshots because move() already mutated the turn counters.
     */
    public int computeEffectiveRoll(Monster m, int roll, int momentumBefore, int normalSpeedBefore) {
        if (m instanceof Dasher)
            return (momentumBefore > 0) ? roll * 3 : roll * 2;
        if (m instanceof MultiTasker)
            return (normalSpeedBefore > 0) ? roll : roll / 2;
        return roll;
    }

    // ── event message ─────────────────────────────────────────────────────────

    /**
     * Returns a one-line description of what happened this turn.
     * Called by TurnController after game.playTurn() has already run.
     */
    public String buildEventMessage(
            Monster current,  Cell rolledCell, int rolledPos, boolean isPlayer,
            int playerDelta,  int opponentDelta,
            boolean wasPlayerShielded, boolean wasOpponentShielded,
            Monster player,   Monster opponent, Card drawn) {

        if (rolledCell instanceof ConveyorBelt) {
            return "⚙  Conveyor Belt at cell " + rolledPos
                 + " → moved to cell " + current.getPosition() + "!";
        }

        if (rolledCell instanceof ContaminationSock) {
            return "☠  Contamination Sock at cell " + rolledPos
                 + "! Moved back to cell " + current.getPosition()
                 + "  |  -" + Constants.SLIP_PENALTY + " ⚡";
        }

        if (drawn != null) {
            return "🃏  Drew: " + drawn.getName() + " — " + CardController.shortEffect(drawn);
        }

        // shield block?
        boolean playerBlocked   = wasPlayerShielded   && !player  .isShielded() && playerDelta   == 0;
        boolean opponentBlocked = wasOpponentShielded && !opponent.isShielded() && opponentDelta == 0;
        if (playerBlocked)   return "🛡  " + player  .getName() + "'s shield blocked the energy loss!";
        if (opponentBlocked) return "🛡  " + opponent.getName() + "'s shield blocked the energy loss!";

        // door / monster cell energy change
        int curDelta = isPlayer ? playerDelta : opponentDelta;
        if (curDelta > 0) return "🚪  +" + curDelta + " energy from door!";
        if (curDelta < 0) return "🚪  "  + curDelta + " energy from door!";
        return "";   // normal cell — no message needed
    }
}
