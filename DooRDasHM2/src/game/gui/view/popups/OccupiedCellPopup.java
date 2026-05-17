package game.gui.view.popups;

import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Shown when the player's dice roll would land them on an occupied cell.
 * The "Roll Again" button closes the popup and immediately retries the roll
 * via the provided Runnable — no extra click needed.
 */
public final class OccupiedCellPopup {

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";
    private static final String GOLD      = "#f1c40f";
    private static final String RED_BTN   = "#c0392b";
    private static final String RED_HOVER = "#e74c3c";

    /**
     * @param occupierName  name of the monster currently standing on the cell
     * @param owner         parent window (for modal attachment)
     * @param onRollAgain   called after the popup closes — triggers a fresh dice roll
     */
    public static void show(String occupierName, Window owner, Runnable onRollAgain) {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) popup.initOwner(owner);
        popup.setResizable(false);

        // Background image (same as the "choose a side" popup)
        ImageView bg = new ImageView();
        Image bgImg  = ResourceLoader.loadImage("start_popup", 460, 290);
        if (bgImg != null) {
            bg.setImage(bgImg);
            bg.setFitWidth(460); bg.setFitHeight(290);
            bg.setPreserveRatio(false); bg.setOpacity(0.20);
        }

        Label icon = new Label("🚧");
        icon.setStyle("-fx-font-size: 40px;");

        Label title = new Label("CELL OCCUPIED!");
        title.setFont(font(F_BANGERS, 30));
        title.setStyle("-fx-text-fill: " + GOLD + ";");

        Label msg = new Label(
            occupierName + " is already standing there.\n" +
            "You can't land on the same cell as your opponent.\n" +
            "Roll again and try a different spot!"
        );
        msg.setFont(font(F_INTER, 13));
        msg.setStyle("-fx-text-fill: #ecf0f1;");
        msg.setWrapText(true);
        msg.setMaxWidth(360);
        msg.setAlignment(Pos.CENTER);
        msg.setTextAlignment(TextAlignment.CENTER);

        Button rollBtn = new Button("Roll the Dice Again");
        rollBtn.setFont(font(F_PIXEL, 9));
        rollBtn.setPrefSize(220, 44);
        applyBtnStyle(rollBtn, RED_BTN);
        rollBtn.setOnMouseEntered(e -> applyBtnStyle(rollBtn, RED_HOVER));
        rollBtn.setOnMouseExited(e  -> applyBtnStyle(rollBtn, RED_BTN));
        rollBtn.setOnAction(e -> {
            popup.close();
            // Platform.runLater ensures handleRoll() runs after showAndWait() has
            // fully unwound — without this, a second InvalidMoveException would try
            // to open another showAndWait() inside the first one, which JavaFX blocks.
            if (onRollAgain != null) javafx.application.Platform.runLater(onRollAgain);
        });

        VBox content = new VBox(12, icon, title, msg, rollBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));

        Rectangle clip = new Rectangle(460, 290);
        clip.setArcWidth(20); clip.setArcHeight(20);

        StackPane root = new StackPane(bg, content);
        root.setClip(clip);
        root.setStyle(
            "-fx-background-color: #0d0d1a;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(155,89,182,0.60);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian,#6c3483,22,0.30,0,0);"
        );

        Scene scene = new Scene(root, 460, 290);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static void applyBtnStyle(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + color + ",14,0.5,0,0);"
        );
    }

    private static Font font(String path, double size) { return ResourceLoader.font(path, size); }
    private OccupiedCellPopup() {}
}
