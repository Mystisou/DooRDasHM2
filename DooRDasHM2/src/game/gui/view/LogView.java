package game.gui.view;

import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

/**
 * Top bar: just "YOUR TURN" / "OPPONENT'S TURN" centred,
 * with a "Review Rules" button pinned to the right.
 */
public class LogView extends HBox {

    private static final String GOLD      = "#f1c40f";
    private static final String CYAN      = "#00bcd4";
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
        logLabel.setMaxWidth(Double.MAX_VALUE);
        logLabel.setAlignment(Pos.CENTER);
        HBox.setHgrow(logLabel, Priority.ALWAYS);

        reviewBtn = new Button("Review Rules");
        reviewBtn.setFont(font(F_INTER, 11));
        reviewBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + CYAN + ";" +
            "-fx-border-color: " + CYAN + "77;" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1.5;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 14;"
        );
        reviewBtn.setOnMouseEntered(e -> reviewBtn.setStyle(
            "-fx-background-color: rgba(0,188,212,0.12);" +
            "-fx-text-fill: " + CYAN + ";" +
            "-fx-border-color: " + CYAN + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1.5;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 14;"
        ));
        reviewBtn.setOnMouseExited(e -> reviewBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + CYAN + ";" +
            "-fx-border-color: " + CYAN + "77;" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1.5;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 14;"
        ));
        // action wired by GameView

        this.getChildren().addAll(logLabel, reviewBtn);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(4, 56, 8, 8));  // extra right pad to clear the X button
        this.setSpacing(10);
    }

    /** Replace the turn label — keep it short: "YOUR TURN!" or "OPPONENT'S TURN!" */
    public void updateLog(String message) { logLabel.setText(message); }

    /** Exposed so GameView can attach the review-rules popup action. */
    public Button getReviewButton() { return reviewBtn; }

    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
}
