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

public class CellController {

    private final Game     game;
    private final GameView view;

    private final boolean[] activatedDoors = new boolean[100];

    public CellController(Game game, GameView view) {
        this.game = game;
        this.view = view;
    }

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

    public int computeEffectiveRoll(Monster m, int roll, int momentumBefore, int normalSpeedBefore) {
        if (m instanceof Dasher)
            return (momentumBefore > 0) ? roll * 3 : roll * 2;
        if (m instanceof MultiTasker)
            return (normalSpeedBefore > 0) ? roll : roll / 2;
        return roll;
    }

    public String buildEventMessage(
            Monster current,  Cell rolledCell, int rolledPos, boolean isPlayer,
            int playerDelta,  int opponentDelta,
            boolean wasPlayerShielded, boolean wasOpponentShielded,
            Monster player,   Monster opponent, Card drawn) {

        String you  = isPlayer ? "You"       : "Opponent";
        String your = isPlayer ? "Your"      : "Opponent's";
        String them = isPlayer ? "Opponent"  : "You";

        if (rolledCell instanceof ConveyorBelt) {
            int extra = current.getPosition() - rolledPos;
            return you + " landed on a Conveyor Belt at cell " + rolledPos
                 + " and moved forward " + extra + " extra cell" + (extra == 1 ? "" : "s")
                 + " to cell " + current.getPosition() + ".";
        }

        if (rolledCell instanceof ContaminationSock) {
            int backward = rolledPos - current.getPosition();
            return you + " landed on a Contamination Sock at cell " + rolledPos
                 + "! Moved back " + backward + " cell" + (backward == 1 ? "" : "s")
                 + " to cell " + current.getPosition()
                 + " and lost " + Constants.SLIP_PENALTY + " energy.";
        }

        if (drawn != null) {
            return you + " drew a card: " + drawn.getName()
                 + " — " + CardController.shortEffect(drawn);
        }

        boolean playerBlocked   = wasPlayerShielded   && !player  .isShielded() && playerDelta   == 0;
        boolean opponentBlocked = wasOpponentShielded && !opponent.isShielded() && opponentDelta == 0;
        if (playerBlocked)   return (isPlayer ? "Your" : "Opponent's") + " shield blocked the energy loss at cell " + rolledPos + "!";
        if (opponentBlocked) return (isPlayer ? "Opponent's" : "Your") + " shield blocked the energy loss at cell " + rolledPos + "!";

        int curDelta  = isPlayer ? playerDelta   : opponentDelta;
        int otherDelta= isPlayer ? opponentDelta : playerDelta;

        StringBuilder sb = new StringBuilder();

        if (rolledCell instanceof DoorCell) {
            DoorCell door = (DoorCell) rolledCell;
            String doorRole = door.getRole().toString();

            if (curDelta > 0) {
                sb.append(you).append(" gained ").append(curDelta)
                  .append(" energy from the ").append(doorRole).append(" door at cell ").append(rolledPos).append("!");
            } else if (curDelta < 0) {
                sb.append(you).append(" lost ").append(Math.abs(curDelta))
                  .append(" energy at the ").append(doorRole).append(" door at cell ").append(rolledPos).append(".");
            } else {
                sb.append(you).append(" landed on the ").append(doorRole)
                  .append(" door at cell ").append(rolledPos).append(" (already exhausted).");
            }

            if (otherDelta > 0) {
                sb.append(" ").append(them).append(" also gained ").append(otherDelta).append(" energy.");
            } else if (otherDelta < 0) {
                sb.append(" ").append(them).append(" also lost ").append(Math.abs(otherDelta)).append(" energy.");
            }
            return sb.toString();
        }

        if (curDelta > 0) {
            sb.append(you).append(" gained ").append(curDelta).append(" energy at cell ").append(rolledPos).append(".");
        } else if (curDelta < 0) {
            sb.append(you).append(" lost ").append(Math.abs(curDelta)).append(" energy at cell ").append(rolledPos).append(".");
        }
        if (otherDelta > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(them).append(" gained ").append(otherDelta).append(" energy.");
        } else if (otherDelta < 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(them).append(" lost ").append(Math.abs(otherDelta)).append(" energy.");
        }

        if (sb.length() == 0) {
            sb.append(you).append(" landed on a normal cell (").append(rolledPos).append("). Nothing happened.");
        }
        return sb.toString();
    }
}