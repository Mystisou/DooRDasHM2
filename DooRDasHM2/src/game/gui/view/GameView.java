package game.gui.view;

import game.gui.ResourceLoader;
import game.gui.view.popups.AlertPopup;
import game.gui.view.popups.FreezePopup;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Master game-screen layout.
 * Composes LogView, BoardView, two MonsterPanelViews, and ActionPanelView.
 * All public methods delegate to the appropriate sub-view.
 */
public class GameView extends BorderPane {

    private static final String BG_DARK  = "#0d0d1a";
    private static final String GOLD     = "#f1c40f";
    private static final String TEXT_DIM = "#95a5a6";
    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    private final LogView          logView;
    private final BoardView        boardView;
    private final MonsterPanelView playerPanel;
    private final MonsterPanelView opponentPanel;
    private final ActionPanelView  actionPanel;

    public GameView() {
        this.setStyle("-fx-background-color: " + BG_DARK + ";");
        this.setPadding(new Insets(10));

        logView       = new LogView();
        boardView     = new BoardView();
        playerPanel   = new MonsterPanelView(true);
        opponentPanel = new MonsterPanelView(false);
        actionPanel   = new ActionPanelView();

        BorderPane.setMargin(playerPanel,   new Insets(0, 12, 0, 0));
        BorderPane.setMargin(opponentPanel, new Insets(0, 0, 0, 12));

        this.setTop(logView);
        this.setLeft(playerPanel);
        this.setRight(opponentPanel);
        this.setCenter(boardView);
        this.setBottom(actionPanel);

        // Wire the Review Rules button in LogView
        logView.getReviewButton().setOnAction(e -> showReviewPopup());
    }

    // ── board delegates ───────────────────────────────────────────────────────

    public void movePlayer(int newPos, boolean isPlayer, Runnable onComplete) {
        boardView.movePlayer(newPos, isPlayer, onComplete);
    }
    public void movePlayer(int newPos, boolean isPlayer) {
        boardView.movePlayer(newPos, isPlayer, null);
    }
    public void setTokenImage(boolean isPlayer, String monsterName) {
        boardView.setTokenImage(isPlayer, monsterName);
    }
    public void setMonsterCellImage(int idx, String monsterName)        { boardView.setMonsterCellImage(idx, monsterName); }
    public void setDoorEnergyLabel(int index, int energy)              { boardView.setDoorEnergyLabel(index, energy); }
    public void addMonsterNameLabel(int idx, String name)        { boardView.addMonsterNameLabel(idx, name);    }
    public void markDoorActivated(int index)                     { boardView.markDoorActivated(index);          }

    // ── monster panel delegates ───────────────────────────────────────────────

    public void updateMonsterCard(boolean isPlayer, String name, String origRole,
                                   String curRole, String type, int energy, int pos, String status) {
        panel(isPlayer).update(name, origRole, curRole, type, energy, pos, status);
    }
    public void setPlayerPhoto(boolean isPlayer, String monsterName, String role) {
        panel(isPlayer).setPhoto(monsterName);
    }
    public void showEnergyDelta(boolean isPlayer, int delta) { panel(isPlayer).showEnergyDelta(delta); }

    // ── action panel delegates ────────────────────────────────────────────────

    public void setDiceTurnIndicator(boolean isPlayerTurn)  { actionPanel.setDiceTurnIndicator(isPlayerTurn); }
    public void showDiceResult(int roll)                    { actionPanel.showDiceResult(roll);               }
    public void resetDiceLabel(boolean isPlayerTurn)        { actionPanel.resetDiceLabel(isPlayerTurn);       }
    public void setPowerEnabled(boolean enabled)            { actionPanel.setPowerEnabled(enabled);           }
    public void updateCardPileCount(int remaining)          { actionPanel.updateCardPileCount(remaining);     }
    public void updateEventText(String text)                { actionPanel.updateEventText(text);              }
    public void clearEventText()                            { actionPanel.clearEventText();                   }
    public Button    getPowerBtn()      { return actionPanel.getPowerBtn();      }
    public ImageView getDiceImageView() { return actionPanel.getDiceImageView(); }

    // ── log delegate ──────────────────────────────────────────────────────────

    /** Keep short: "YOUR TURN!" or "OPPONENT'S TURN!" */
    public void updateLog(String message) { logView.updateLog(message); }

    // ── popups ────────────────────────────────────────────────────────────────

    public void showAlert(String title, String message) {
        AlertPopup.show(title, message, getScene() != null ? getScene().getWindow() : null);
    }
    public void showFreezeSkip(String monsterName) {
        FreezePopup.show(monsterName, getScene() != null ? getScene().getWindow() : null);
    }

    /** Styled "Not Enough Energy" popup matching the StartView side-selection popup. */
    public void showNotEnoughEnergyPopup() {
        javafx.stage.Stage popup = new javafx.stage.Stage();
        popup.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        if (getScene() != null) popup.initOwner(getScene().getWindow());
        popup.setResizable(false);

        ImageView bg = new ImageView();
        javafx.scene.image.Image bgImg = ResourceLoader.loadImage("start_popup", 420, 260);
        if (bgImg != null) {
            bg.setImage(bgImg); bg.setFitWidth(420); bg.setFitHeight(260);
            bg.setPreserveRatio(false); bg.setOpacity(0.20);
        }

        Label title = new Label("NOT ENOUGH ENERGY!");
        title.setFont(font(F_BANGERS, 28));
        title.setStyle("-fx-text-fill: " + GOLD + ";");

        Label msg = new Label("You need at least 500 energy\nto activate your power-up.");
        msg.setFont(font(F_INTER, 13));
        msg.setStyle("-fx-text-fill: #ecf0f1;");
        msg.setAlignment(javafx.geometry.Pos.CENTER);
        msg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button okBtn = new Button("GOT IT");
        okBtn.setFont(font(F_BANGERS, 18));
        okBtn.setPrefSize(160, 42);
        okBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(gaussian,#c0392b,14,0.5,0,0);");
        okBtn.setOnMouseEntered(e -> okBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(gaussian,#e74c3c,14,0.5,0,0);"));
        okBtn.setOnMouseExited(e  -> okBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(gaussian,#c0392b,14,0.5,0,0);"));
        okBtn.setOnAction(e -> popup.close());

        VBox content = new VBox(14, title, msg, okBtn);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setPadding(new Insets(30));

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(bg, content);
        root.setStyle("-fx-background-color: #0d0d1a; -fx-background-radius: 16; -fx-border-color: rgba(155,89,182,0.60); -fx-border-radius: 16; -fx-border-width: 2;");

        javafx.scene.Scene scene = new javafx.scene.Scene(root, 420, 260);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // ── review rules popup ────────────────────────────────────────────────────

    private void showReviewPopup() {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.initModality(Modality.APPLICATION_MODAL);
        if (getScene() != null) popup.initOwner(getScene().getWindow());
        popup.setResizable(false);

        Label bookIcon = new Label("📖"); bookIcon.setStyle("-fx-font-size: 28px;");
        Label hdr = new Label("QUICK RULES"); hdr.setFont(font(F_BANGERS, 30)); hdr.setStyle("-fx-text-fill: " + GOLD + ";");
        VBox header = new VBox(4, bookIcon, hdr); header.setAlignment(Pos.CENTER); header.setPadding(new Insets(0,0,8,0));

        VBox content = new VBox(10);
        content.setPadding(new Insets(2, 6, 2, 6));
        content.getChildren().addAll(
            qs("The Goal",       "Reach Cell 99 with 1,000+ energy to win."),
            qs("Your Turn",      "Optionally activate powerup (costs 500 energy), then roll and move."),
            qs("Occupied Cell",  "Can't land on your opponent — roll again."),
            qs("Doors",          "Match your role → team gains energy. Wrong role → team loses it. One-use only."),
            qs("Monster Cells",  "Same role → free powerup. Opposite role, more energy → energies swap."),
            qs("Conveyor Belts", "Jump forward automatically. Landing cell doesn't activate."),
            qs("Socks",          "Move backwards and lose 100 energy. Shield saves the energy, not the move."),
            qs("Card Cells",     "Draw a random card — affects both players."),
            qs("Shield",         "Blocks next energy loss for your team. Schemer's steal ignores it."),
            qs("Confusion",      "Roles flip for a few turns — wrong doors will hurt you!"),
            qs("Powerups",
               "Dasher: 3x speed 3 turns  |  Dynamo: freeze opponent  |  " +
               "Multitasker: normal speed 2 turns  |  Schemer: steal from everyone")
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefHeight(370);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        Button closeBtn = new Button("GOT IT!");
        closeBtn.setFont(font(F_BANGERS, 18));
        closeBtn.setPrefSize(180, 40);
        closeBtn.setStyle("-fx-background-color: " + GOLD + "; -fx-text-fill: #0d0d1a; -fx-background-radius: 10; -fx-cursor: hand; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> popup.close());
        HBox btnRow = new HBox(closeBtn); btnRow.setAlignment(Pos.CENTER); btnRow.setPadding(new Insets(12,0,2,0));

        Separator sep = new Separator(); sep.setStyle("-fx-background-color: rgba(155,89,182,0.30);");
        VBox outer = new VBox(10, header, sep, scroll, btnRow);
        outer.setPadding(new Insets(24, 24, 20, 24));

        Rectangle clip = new Rectangle(520, 560); clip.setArcWidth(20); clip.setArcHeight(20);
        StackPane root = new StackPane(outer); root.setClip(clip);
        root.setStyle("-fx-background-color: #0d0d1a; -fx-background-radius: 16; -fx-border-color: rgba(155,89,182,0.55); -fx-border-radius: 16; -fx-border-width: 2; -fx-effect: dropshadow(gaussian,#6c3483,26,0.32,0,0);");

        // position centred on screen
        Screen screen = Screen.getPrimary();
        popup.setX((screen.getVisualBounds().getWidth()  - 520) / 2);
        popup.setY((screen.getVisualBounds().getHeight() - 560) / 2);

        popup.setScene(new javafx.scene.Scene(root, 520, 560) {{ setFill(Color.TRANSPARENT); }});
        popup.showAndWait();
    }

    private VBox qs(String title, String body) {
        Label t = new Label(title); t.setFont(font(F_BANGERS, 18)); t.setStyle("-fx-text-fill: " + GOLD + ";");
        Label b = new Label(body);  b.setFont(font(F_INTER, 11));   b.setStyle("-fx-text-fill: #ecf0f1; -fx-line-spacing: 1;"); b.setWrapText(true);
        VBox s = new VBox(3, t, b); s.setPadding(new Insets(7, 12, 7, 12));
        s.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8; -fx-border-color: rgba(155,89,182,0.18); -fx-border-radius: 8; -fx-border-width: 1;");
        return s;
    }

    private MonsterPanelView panel(boolean isPlayer) { return isPlayer ? playerPanel : opponentPanel; }
    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
}
