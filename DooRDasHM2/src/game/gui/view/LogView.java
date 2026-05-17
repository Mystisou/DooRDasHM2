package game.gui.view;

import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LogView extends VBox {

    private static final String GOLD  = "#f1c40f";
    private static final String CYAN  = "#00bcd4";
    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    private final Label  logLabel;
    private final Button reviewBtn;

    public LogView() {
        logLabel = new Label("YOUR TURN!");
        logLabel.setFont(font(F_BANGERS, 26));
        logLabel.setStyle(
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-background-color: rgba(108,52,131,0.18);" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(155,89,182,0.50);" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1.5;" +
            "-fx-padding: 6 28;" +
            "-fx-effect: dropshadow(gaussian,#6c3483,10,0.30,0,0);"
        );
        logLabel.setMaxWidth(460);
        logLabel.setAlignment(Pos.CENTER);

        reviewBtn = new Button("Review Rules");
        reviewBtn.setFont(font(F_INTER, 11));
        styleReviewBtn(false);
        reviewBtn.setOnMouseEntered(e -> styleReviewBtn(true));
        reviewBtn.setOnMouseExited(e  -> styleReviewBtn(false));

        
        StackPane bar = new StackPane(logLabel, reviewBtn);
        StackPane.setAlignment(logLabel,    Pos.CENTER);
        StackPane.setAlignment(reviewBtn,   Pos.CENTER_RIGHT);
        StackPane.setMargin(reviewBtn, new Insets(0, 60, 0, 0));

        this.getChildren().add(bar);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(4, 0, 10, 0));
    }

    public void updateLog(String message) { logLabel.setText(message); }
    public Button getReviewButton()       { return reviewBtn; }

    private void styleReviewBtn(boolean hover) {
        reviewBtn.setStyle(
            (hover ? "-fx-background-color: rgba(0,188,212,0.12);" : "-fx-background-color: transparent;") +
            "-fx-text-fill: " + CYAN + ";" +
            "-fx-border-color: " + CYAN + (hover ? ";" : "77;") +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1.5;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 14;"
        );
    }

    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
}
