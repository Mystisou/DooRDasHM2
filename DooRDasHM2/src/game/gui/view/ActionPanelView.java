package game.gui.view;

import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Bottom action bar.
 *
 * Layout:
 *   [dice result box ]   [ event result text (gained +120...) ]   [ POWER button w/ photo ]
 *   [cards remaining ]
 *
 * The top log (LogView) now shows only "YOUR TURN" / "OPPONENT'S TURN".
 */
public class ActionPanelView extends HBox {

    private static final String BG_DARK   = "#0d0d1a";
    private static final String GOLD      = "#f1c40f";
    private static final String TEXT_DIM  = "#95a5a6";
    private static final String CYAN      = "#00bcd4";
    private static final String RED_BTN   = "#c0392b";
    private static final String RED_HOVER = "#e74c3c";

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    // exposed for wiring
    private Button    powerBtn;
    private ImageView diceClickTarget;   // invisible overlay — GameController wires mouse click here

    // state labels
    private Label diceResultLbl;
    private Label cardsRemainingLbl;
    private Label eventTextLbl;

    public ActionPanelView() { build(); }

    // ── public update API ────────────────────────────────────────────────────

    public void showDiceResult(int roll) {
        diceResultLbl.setText(String.valueOf(roll));
    }

    public void resetDiceLabel(boolean isPlayerTurn) {
        diceResultLbl.setText("—");
    }

    public void updateCardPileCount(int remaining) {
        cardsRemainingLbl.setText(String.valueOf(remaining));
    }

    public void setPowerEnabled(boolean enabled) {
        powerBtn.setDisable(!enabled);
        powerBtn.setOpacity(enabled ? 1.0 : 0.40);
    }

    public void setDiceTurnIndicator(boolean isPlayerTurn) {
        DropShadow glow = new DropShadow(22, isPlayerTurn ? Color.CYAN : Color.MAGENTA);
        glow.setSpread(0.35);
        diceClickTarget.setEffect(glow);
        diceClickTarget.setOpacity(isPlayerTurn ? 1.0 : 0.65);
    }

    /** Shows "Gained +120 energy from door!" style text between the boxes and power button. */
    public void updateEventText(String text) {
        eventTextLbl.setText(text);
    }

    public void clearEventText() { eventTextLbl.setText(""); }

    public Button    getPowerBtn()         { return powerBtn;        }
    public ImageView getDiceImageView()    { return diceClickTarget; }

    // ── layout ───────────────────────────────────────────────────────────────

    private void build() {
        // Left: power button
        StackPane powerPane = buildPowerButton();

        // Centre: event result text (grows to fill space)
        eventTextLbl = new Label("");
        eventTextLbl.setFont(font(F_INTER, 13));
        eventTextLbl.setStyle("-fx-text-fill: " + GOLD + ";");
        eventTextLbl.setWrapText(true);
        eventTextLbl.setMaxWidth(240);
        eventTextLbl.setAlignment(Pos.CENTER);
        eventTextLbl.setTextAlignment(TextAlignment.CENTER);
        HBox centre = new HBox(eventTextLbl);
        centre.setAlignment(Pos.CENTER);
        HBox.setHgrow(centre, Priority.ALWAYS);

        // Right: stacked — [small: dice image + cards count] on top,
        //                   [big: roll result number] on bottom
        VBox rightCol = buildRightColumn();

        this.getChildren().addAll(powerPane, centre, rightCol);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(14);
        this.setPadding(new Insets(8, 12, 8, 12));
        this.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-border-color: rgba(155,89,182,0.30) transparent transparent transparent;" +
            "-fx-border-width: 1.5;"
        );
    }

    private VBox buildRightColumn() {
        // ── Top box: clickable dice image + "Cards: N" ──────────────────────
        Image diceImg = ResourceLoader.loadImage("dice", 40, 40);
        diceClickTarget = (diceImg != null) ? new ImageView(diceImg) : new ImageView();
        diceClickTarget.setFitWidth(40); diceClickTarget.setFitHeight(40);
        diceClickTarget.setClip(new Circle(20, 20, 20));
        diceClickTarget.setStyle("-fx-cursor: hand;");

        cardsRemainingLbl = new Label("25");
        cardsRemainingLbl.setFont(font(F_BANGERS, 20));
        cardsRemainingLbl.setStyle("-fx-text-fill: " + GOLD + ";");

        Label cardsTitle = new Label("Cards");
        cardsTitle.setFont(font(F_INTER, 10));
        cardsTitle.setStyle("-fx-text-fill: " + TEXT_DIM + "; -fx-opacity: 0.75;");

        VBox cardsLblBox = new VBox(0, cardsRemainingLbl, cardsTitle);
        cardsLblBox.setAlignment(Pos.CENTER_LEFT);

        HBox topBox = new HBox(10, diceClickTarget, cardsLblBox);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(5, 10, 5, 10));
        topBox.setStyle(
            "-fx-background-color: rgba(108,52,131,0.18);" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: rgba(155,89,182,0.40);" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        // ── Bottom box: big rolled number display ────────────────────────────
        diceResultLbl = new Label("—");
        diceResultLbl.setFont(font(F_BANGERS, 44));
        diceResultLbl.setStyle("-fx-text-fill: " + GOLD + ";");
        diceResultLbl.setAlignment(Pos.CENTER);

        Label rollTitle = new Label("ROLLED");
        rollTitle.setFont(font(F_PIXEL, 7));
        rollTitle.setStyle("-fx-text-fill: " + TEXT_DIM + "; -fx-opacity: 0.7;");

        VBox bottomBox = new VBox(2, rollTitle, diceResultLbl);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(8, 18, 8, 18));
        bottomBox.setStyle(
            "-fx-background-color: rgba(108,52,131,0.25);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(155,89,182,0.55);" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;"
        );

        VBox col = new VBox(6, topBox, bottomBox);
        col.setAlignment(Pos.CENTER_LEFT);
        return col;
    }

    /**
     * Power button: circular monster-ability photo on top,
     * "ACTIVATE POWER" label below, styled with red glow.
     */
    private StackPane buildPowerButton() {
        Image powerImg = ResourceLoader.loadImage("energy", 52, 52);
        ImageView powerIv = (powerImg != null) ? new ImageView(powerImg) : new ImageView();
        powerIv.setFitWidth(52); powerIv.setFitHeight(52);
        Circle ivClip = new Circle(26, 26, 26);
        powerIv.setClip(ivClip);

        Label btnLabel = new Label("ACTIVATE\nPOWER");
        btnLabel.setFont(font(F_PIXEL, 7));
        btnLabel.setStyle("-fx-text-fill: white;");
        btnLabel.setAlignment(Pos.CENTER);
        btnLabel.setTextAlignment(TextAlignment.CENTER);

        VBox inner = new VBox(5, powerIv, btnLabel);
        inner.setAlignment(Pos.CENTER);
        inner.setPadding(new Insets(10, 14, 10, 14));

        powerBtn = new Button();
        powerBtn.setGraphic(inner);
        powerBtn.setPrefSize(110, 90);
        powerBtn.setStyle(
            "-fx-background-color: " + RED_BTN + ";" +
            "-fx-background-radius: 14;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + RED_BTN + ",16,0.5,0,0);"
        );
        powerBtn.setOnMouseEntered(e -> powerBtn.setStyle(
            "-fx-background-color: " + RED_HOVER + ";" +
            "-fx-background-radius: 14;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + RED_HOVER + ",20,0.6,0,0);"
        ));
        powerBtn.setOnMouseExited(e -> powerBtn.setStyle(
            "-fx-background-color: " + RED_BTN + ";" +
            "-fx-background-radius: 14;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + RED_BTN + ",16,0.5,0,0);"
        ));

        // wrap in StackPane so we can position it
        StackPane wrap = new StackPane(powerBtn);
        wrap.setAlignment(Pos.CENTER);
        return wrap;
    }

    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
}
