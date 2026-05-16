package game.gui.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ViewManager {
    private static Stage ViewStage;
    private static final double W = 1000;
    private static final double H = 800;

    public static void setStage(Stage s) { ViewStage = s; }

    public static void updateView(Pane newLayout) {
        Button closeBtn = new Button("X");
        closeBtn.setStyle(
            "-fx-background-color: #e74c3c; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-font-size: 18px; -fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> Platform.exit());

        StackPane root = new StackPane();
        root.getChildren().addAll(newLayout, closeBtn);
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new Insets(15, 15, 0, 0));

        if (ViewStage.getScene() == null) {
            // First call: create the scene at fixed size.
            // Stage stays RESIZABLE so the user can maximise/minimise normally.
            ViewStage.setScene(new Scene(root, W, H));
        } else {
            // All subsequent navigations: ONLY swap the root.
            // Never touch width/height → zero visible resize flicker.
            ViewStage.getScene().setRoot(root);
        }
    }
}
