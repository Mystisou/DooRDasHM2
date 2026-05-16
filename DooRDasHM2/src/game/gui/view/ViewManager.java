package game.gui.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ViewManager {
    private static Stage ViewStage;

    public static void setStage(Stage s) {
        ViewStage = s;
    }

    public static void updateView(Pane newLayout) {
        Button globalCloseBtn = new Button("X");
        globalCloseBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-cursor: hand;");
        globalCloseBtn.setOnAction(e -> Platform.exit());

        StackPane rootWrapper = new StackPane();
        rootWrapper.getChildren().addAll(newLayout, globalCloseBtn);

        StackPane.setAlignment(globalCloseBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(globalCloseBtn, new Insets(15, 15, 0, 0));

        // Use screen visual bounds so the window always fits on screen
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double w = Math.min(1280, screen.getWidth()  * 0.96);
        double h = Math.min(900,  screen.getHeight() * 0.94);

        if (ViewStage.getScene() == null) {
            ViewStage.setScene(new Scene(rootWrapper, w, h));
        } else {
            ViewStage.getScene().setRoot(rootWrapper);
            ViewStage.setWidth(w);
            ViewStage.setHeight(h);
        }

        // Centre the stage on screen
        ViewStage.setX(screen.getMinX() + (screen.getWidth()  - w) / 2.0);
        ViewStage.setY(screen.getMinY() + (screen.getHeight() - h) / 2.0);
    }
}
