package game.gui.view.popups;

import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
 * Blocking modal popup shown when a monster's turn is skipped by the Freeze effect.
 * Closing it does NOT terminate the game.
 */
public final class FreezePopup {

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    public static void show(String monsterName, Window owner) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);
        if (owner != null) popup.initOwner(owner);
        popup.setResizable(false);

        Label icon = new Label("❄");
        icon.setStyle("-fx-font-size: 48px;");

        Label title = new Label("Turn Frozen!");
        title.setFont(font(F_BANGERS, 30));
        title.setStyle("-fx-text-fill: #74b9ff;");

        Label msg = new Label(monsterName + " is FROZEN and cannot move.\nTheir entire turn is skipped.");
        msg.setFont(font(F_INTER, 13));
        msg.setStyle("-fx-text-fill: #ecf0f1;");
        msg.setWrapText(true);
        msg.setMaxWidth(320);
        msg.setAlignment(Pos.CENTER);
        msg.setTextAlignment(TextAlignment.CENTER);

        String btnBase  = "-fx-background-color: #0984e3; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(gaussian,#0984e3,12,0.5,0,0);";
        String btnHover = "-fx-background-color: #74b9ff; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;";
        Button okBtn = new Button("OK");
        okBtn.setFont(font(F_PIXEL, 9));
        okBtn.setPrefSize(120, 38);
        okBtn.setStyle(btnBase);
        okBtn.setOnMouseEntered(e -> okBtn.setStyle(btnHover));
        okBtn.setOnMouseExited(e  -> okBtn.setStyle(btnBase));
        okBtn.setOnAction(e -> popup.close());

        VBox content = new VBox(12, icon, title, msg, okBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28, 32, 28, 32));

        Rectangle clip = new Rectangle(380, 260);
        clip.setArcWidth(24);
        clip.setArcHeight(24);

        StackPane root = new StackPane(content);
        root.setClip(clip);
        root.setStyle(
            "-fx-background-color: #0d0d1a;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(116,185,255,0.60);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian,#0984e3,22,0.35,0,0);"
        );

        Scene scene = new Scene(root, 380, 260);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static Font font(String path, double size) { return ResourceLoader.font(path, size); }
    private FreezePopup() {}
}
