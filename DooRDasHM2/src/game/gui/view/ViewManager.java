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

        if (ViewStage.getScene() == null)
            ViewStage.setScene(new Scene(rootWrapper, 1000, 800));
        else
            ViewStage.getScene().setRoot(rootWrapper);
    }
}
