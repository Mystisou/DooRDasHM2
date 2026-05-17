package game.gui.controller;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.cells.Cell;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.monsters.Dasher;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import game.gui.view.EndGamePopup;
import game.gui.view.GameView;
import game.gui.view.StartView;
import game.gui.view.ViewManager;
import game.gui.view.popups.FreezePopup;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;

public class GameController {

    private final Game     game;
    private final GameView view;

    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;

        CardController    cardCtrl  = new CardController();
        CellController    cellCtrl  = new CellController(game, view);
        PowerupController powerCtrl = new PowerupController(game, view, this);
        TurnController    turnCtrl  = new TurnController(game, view, this, cellCtrl, cardCtrl);

        initView();
        wireHandlers(turnCtrl, powerCtrl);
    }

    private void initView() {
        Monster p = game.getPlayer();
        Monster o = game.getOpponent();

        view.setTokenImage(true,  p.getName());
        view.setTokenImage(false, o.getName());

        view.movePlayer(p.getPosition(), true);
        view.movePlayer(o.getPosition(), false);

        view.setPlayerPhoto(true,  p.getName(), p.getOriginalRole().toString());
        view.setPlayerPhoto(false, o.getName(), o.getOriginalRole().toString());

        for (int i = 1; i < 100; i += 2) {
            Cell c = game.getBoard().getCell(i);
            if (c instanceof DoorCell)
                view.setDoorEnergyLabel(i, ((DoorCell) c).getEnergy());
        }

        for (int idx : Constants.MONSTER_CELL_INDICES) {
            Cell c = game.getBoard().getCell(idx);
            if (c instanceof MonsterCell) {
                Monster m = ((MonsterCell) c).getCellMonster();
                if (m != null) {
                    view.setMonsterCellImage(idx, m.getName());
                    view.addMonsterNameLabel(idx, abbrev(m.getName()));
                }
            }
        }

        refreshStats();
        view.updateCardPileCount(Board.cards.size());
        setTurnState("YOUR TURN!", true);
    }

    private void wireHandlers(TurnController turnCtrl, PowerupController powerCtrl) {
        view.getDiceImageView().setOnMouseClicked(e -> turnCtrl.handleRoll());
        view.getPowerBtn()     .setOnAction      (e -> powerCtrl.handlePower());

        view.getDiceImageView().sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null)
                newScene.setOnKeyPressed(e -> handleDebugKey(e.getCode(), turnCtrl));
        });
    }

    private void handleDebugKey(KeyCode code, TurnController turnCtrl) {
        Monster current  = game.getCurrent();
        Monster player   = game.getPlayer();
        boolean isPlayer = (current == player);
        if (code == KeyCode.W) {
            current.setPosition(Constants.WINNING_POSITION);
            view.movePlayer(99, isPlayer, () ->
                Platform.runLater(() -> {
                    refreshStats();
                    if (current.getEnergy() >= 1000)
                        navigateToWin(current);
                })
            );
        } else if (code == KeyCode.E) {
            current.setEnergy(1000);
            refreshStats();
            if(current.getPosition()==99)
               navigateToWin(current);
        } else if (code == KeyCode.F) {
            Window owner = view.getScene() != null ? view.getScene().getWindow() : null;
            Platform.runLater(() ->
                FreezePopup.show(current.getName(), owner)
            );
        }
    }

    public void refreshStats() {
        Monster p = game.getPlayer();
        Monster o = game.getOpponent();
        view.updateMonsterCard(true,  p.getName(), p.getOriginalRole().toString(), p.getRole().toString(), p.getClass().getSimpleName(), p.getEnergy(), p.getPosition(), buildStatus(p));
        view.updateMonsterCard(false, o.getName(), o.getOriginalRole().toString(), o.getRole().toString(), o.getClass().getSimpleName(), o.getEnergy(), o.getPosition(), buildStatus(o));
    }

    public void setTurnState(String message, boolean isPlayerTurn) {
        view.updateLog(message);
        view.setDiceTurnIndicator(isPlayerTurn);
        view.setPowerEnabled(isPlayerTurn);
    }

    public void navigateToWin(Monster winner) {
        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();
        boolean playerWon = (winner == player);
        Window owner = view.getScene() != null ? view.getScene().getWindow() : null;
        EndGamePopup.show(
            playerWon,
            player  .getName(), player  .getRole().toString(), player  .getEnergy(),
            opponent.getName(), opponent.getRole().toString(), opponent.getEnergy(),
            owner,
            () -> { StartView sv = new StartView(); new StartController(sv); ViewManager.updateView(sv); }
        );
    }

    public void navigateToLoss(Monster winner) {
        navigateToWin(winner);
    }

    public String buildStatus(Monster m) {
        StringBuilder sb = new StringBuilder();
        if (m.isShielded()) sb.append("Shield  ");
        if (m.isConfused()) sb.append("Confused(").append(m.getConfusionTurns()).append(")  ");
        if (m.isFrozen())   sb.append("Frozen  ");
        if (m instanceof Dasher)       { int r = ((Dasher)       m).getMomentumTurns();    if (r > 0) sb.append("Rush(").append(r).append(")  "); }
        if (m instanceof MultiTasker)  { int f = ((MultiTasker)  m).getNormalSpeedTurns(); if (f > 0) sb.append("Focus(").append(f).append(")  "); }
        return sb.length() == 0 ? "Normal" : sb.toString().trim();
    }

    private String abbrev(String n) { String f = n.split(" ")[0]; return f.length() > 7 ? f.substring(0,6)+"." : f; }
}