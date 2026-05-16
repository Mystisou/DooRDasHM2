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

        boolean wasMaximized = ViewStage.isMaximized();

        // Compute a sensible default size (used when not maximized)
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double w = Math.min(1100, screen.getWidth()  * 0.90);
        double h = Math.min(860,  screen.getHeight() * 0.94);

        if (ViewStage.getScene() == null) {
            ViewStage.setScene(new Scene(rootWrapper, w, h));
            ViewStage.setX(screen.getMinX() + (screen.getWidth()  - w) / 2.0);
            ViewStage.setY(screen.getMinY() + (screen.getHeight() - h) / 2.0);
        } else {
            // Un-maximize briefly so we can resize, then restore
            if (wasMaximized) ViewStage.setMaximized(false);
            ViewStage.getScene().setRoot(rootWrapper);
            if (!wasMaximized) {
                ViewStage.setWidth(w);
                ViewStage.setHeight(h);
                ViewStage.setX(screen.getMinX() + (screen.getWidth()  - w) / 2.0);
                ViewStage.setY(screen.getMinY() + (screen.getHeight() - h) / 2.0);
            }
            if (wasMaximized) ViewStage.setMaximized(true);
        }
    }
}
