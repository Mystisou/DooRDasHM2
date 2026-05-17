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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class ActionPanelView extends HBox {

    private static final String BG_DARK   = "#0d0d1a";
    private static final String GOLD      = "#f1c40f";
    private static final String TEXT_DIM  = "#95a5a6";
    private static final String PURPLE    = "rgba(155,89,182,0.50)";
    private static final String PURPLE_BG = "rgba(108,52,131,0.18)";

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    private Button powerBtn;
    private ImageView diceImageView;
    private StackPane diceWrapper;

    private Label cardsNumLabel;
    private Label rollNumLabel;
    private Label eventLogLabel;

    private StackPane photoPowerPane;

    public ActionPanelView() { build(); }

    public void showDiceResult(int roll) {
        rollNumLabel.setText(String.valueOf(roll));
    }

    public void resetDiceLabel(boolean isPlayerTurn) {
        rollNumLabel.setText("—");
        eventLogLabel.setText(isPlayerTurn ? "Your turn — roll the dice!" : "Opponent's turn — roll for them.");
    }

    public void updateEventText(String text) { eventLogLabel.setText(text); }
    public void clearEventText()              { eventLogLabel.setText(""); }

    public void updateCardPileCount(int remaining) {
        cardsNumLabel.setText(String.valueOf(remaining));
    }

    public void setPowerEnabled(boolean enabled) {
        photoPowerPane.setOpacity(enabled ? 1.0 : 0.40);
        powerBtn.setDisable(!enabled);
    }

    public void setDiceTurnIndicator(boolean isPlayerTurn) {
        DropShadow glow = new DropShadow(26, isPlayerTurn ? Color.CYAN : Color.RED);
        glow.setSpread(0.40);
        diceWrapper.setEffect(glow);
        diceWrapper.setOpacity(isPlayerTurn ? 1.0 : 0.65);
    }

    private StackPane buildPhotoPane() {
        StackPane pane = new StackPane();
        pane.setMinSize(76, 76); pane.setMaxSize(76, 76);
        pane.setStyle(
            "-fx-background-color: #0d0d1a;" +
            "-fx-background-radius: 38;" +
            "-fx-border-color: #c0392bBB;" +
            "-fx-border-width: 2.5;" +
            "-fx-border-radius: 38;" +
            "-fx-effect: dropshadow(gaussian,#c0392b,14,0.4,0,0);" +
            "-fx-cursor: hand;"
        );

        Image energyImg = ResourceLoader.loadImage("energy", 56, 56);
        if (energyImg != null) {
            ImageView iv = new ImageView(energyImg);
            iv.setFitWidth(56); iv.setFitHeight(56);
            iv.setClip(new Circle(28, 28, 28));
            iv.setEffect(new DropShadow(10, Color.web("#c0392b", 0.7)));
            pane.getChildren().add(iv);
        } else {
            Label fb = new Label("PWR");
            fb.setFont(font(F_BANGERS, 18));
            fb.setStyle("-fx-text-fill: #c0392b;");
            pane.getChildren().add(fb);
        }
        return pane;
    }

    public Button    getPowerBtn()      { return powerBtn;      }
    public ImageView getDiceImageView() { return diceImageView; }

    private void build() {
        HBox leftCol = buildLeftInfoCol();

        eventLogLabel = new Label("Roll the dice to begin!");
        eventLogLabel.setFont(font(F_INTER, 13));
        eventLogLabel.setStyle(
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-background-color: " + PURPLE_BG + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + PURPLE + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;" +
            "-fx-padding: 8 14;"
        );
        eventLogLabel.setWrapText(true);
        eventLogLabel.setMinWidth(300);
        eventLogLabel.setPrefWidth(340);
        eventLogLabel.setMaxWidth(360);
        eventLogLabel.setAlignment(Pos.CENTER);
        eventLogLabel.setTextAlignment(TextAlignment.CENTER);
        HBox.setHgrow(eventLogLabel, Priority.ALWAYS);

        Image diceImg = ResourceLoader.loadImage("dice", 72, 72);
        diceImageView = (diceImg != null) ? new ImageView(diceImg) : new ImageView();
        diceImageView.setFitWidth(72); diceImageView.setFitHeight(72);
        diceImageView.setClip(new Circle(36, 36, 36));
        diceImageView.setStyle("-fx-cursor: hand;");

        diceWrapper = new StackPane(diceImageView);
        diceWrapper.setMinSize(76, 76); diceWrapper.setMaxSize(76, 76);
        diceWrapper.setStyle("-fx-cursor: hand;");
        setDiceTurnIndicator(true);

        photoPowerPane = buildPhotoPane();

        powerBtn = new Button();
        powerBtn.setMinSize(76, 76); powerBtn.setMaxSize(76, 76);
        powerBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        photoPowerPane.getChildren().add(powerBtn);

        this.getChildren().addAll(leftCol, eventLogLabel, diceWrapper, photoPowerPane);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(14);
        this.setPadding(new Insets(10, 16, 8, 16));
        this.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-border-color: rgba(155,89,182,0.30) transparent transparent transparent;" +
            "-fx-border-width: 1.5;"
        );
    }

    private HBox buildLeftInfoCol() {
        Label cardsTitle = new Label("CARDS");
        cardsTitle.setFont(font(F_PIXEL, 6));
        cardsTitle.setStyle("-fx-text-fill: " + TEXT_DIM + "; -fx-opacity: 0.85;");

        cardsNumLabel = new Label("25");
        cardsNumLabel.setFont(font(F_BANGERS, 30));
        cardsNumLabel.setStyle("-fx-text-fill: white;");

        VBox cardsBox = new VBox(2, cardsTitle, cardsNumLabel);
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.setPadding(new Insets(6, 14, 6, 14));
        cardsBox.setStyle(
            "-fx-background-color: " + PURPLE_BG + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + PURPLE + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;"
        );

        Label rolledTitle = new Label("ROLLED");
        rolledTitle.setFont(font(F_PIXEL, 6));
        rolledTitle.setStyle("-fx-text-fill: " + TEXT_DIM + "; -fx-opacity: 0.85;");

        rollNumLabel = new Label("—");
        rollNumLabel.setFont(font(F_BANGERS, 30));
        rollNumLabel.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold;");

        VBox rollBox = new VBox(2, rolledTitle, rollNumLabel);
        rollBox.setAlignment(Pos.CENTER);
        rollBox.setPadding(new Insets(6, 14, 6, 14));
        rollBox.setStyle(
            "-fx-background-color: rgba(108,52,131,0.10);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(241,196,15,0.45);" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;"
        );

        HBox row = new HBox(8, cardsBox, rollBox);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
}