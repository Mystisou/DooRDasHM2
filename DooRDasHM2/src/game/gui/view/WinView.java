package game.gui.view;

import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Game-won screen.
 * Pure UI — no navigation logic.
 * GameController wires getReturnButton() to navigate back to StartView.
 */
public class WinView extends VBox {

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    private final Button returnBtn;

    public WinView(String winnerName, String winnerRole, int winnerEnergy,
                   String loserName,  int loserEnergy) {

        this.setAlignment(Pos.CENTER);
        this.setSpacing(22);
        this.setStyle("-fx-background-color: #1a252f;");
        this.setPadding(new Insets(60));

        Label trophy   = new Label("🏆");
        trophy.setStyle("-fx-font-size: 72px;");

        Label headline = new Label("GAME OVER!");
        headline.setFont(font(F_BANGERS, 52));
        headline.setStyle("-fx-text-fill: #f1c40f;");

        String roleColour = winnerRole.equals("SCARER") ? "#3498db" : "#2ecc71";
        Label winnerLbl = new Label(winnerName + " WINS!");
        winnerLbl.setFont(font(F_BANGERS, 34));
        winnerLbl.setStyle("-fx-text-fill: " + roleColour + ";");

        Label roleLbl = new Label("Role: " + winnerRole + "  |  Final Energy: " + winnerEnergy);
        roleLbl.setFont(font(F_INTER, 14));
        roleLbl.setStyle("-fx-text-fill: #bdc3c7;");

        HBox cardsRow = new HBox(50,
            buildResultCard(winnerName, winnerEnergy, true),
            buildResultCard(loserName,  loserEnergy,  false));
        cardsRow.setAlignment(Pos.CENTER);

        returnBtn = new Button("↩  Return to Start");
        returnBtn.setFont(font(F_PIXEL, 10));
        returnBtn.setPrefSize(240, 48);
        returnBtn.setStyle(
            "-fx-background-color: #3498db; -fx-text-fill: white;" +
            "-fx-background-radius: 10; -fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian,#3498db,14,0.5,0,0);"
        );
        // action wired by GameController

        Region divider = new Region();
        divider.setPrefHeight(1); divider.setMaxWidth(400);
        divider.setStyle("-fx-background-color: rgba(155,89,182,0.35);");

        this.getChildren().addAll(trophy, headline, divider, winnerLbl, roleLbl, cardsRow, returnBtn);
    }

    /** Exposed so GameController can wire the "return to start" navigation. */
    public Button getReturnButton() { return returnBtn; }

    private VBox buildResultCard(String name, int energy, boolean isWinner) {
        Label nameLbl   = new Label(name);
        nameLbl.setFont(font(F_BANGERS, 22));
        nameLbl.setStyle("-fx-text-fill: " + (isWinner ? "#f1c40f" : "#bdc3c7") + ";");

        Label energyLbl = new Label("⚡ " + energy + " energy");
        energyLbl.setFont(font(F_INTER, 13));
        energyLbl.setStyle("-fx-text-fill: #2ecc71;");

        Label badge     = new Label(isWinner ? "🏆 WINNER" : "💀 DEFEATED");
        badge.setFont(font(F_INTER, 12));
        badge.setStyle("-fx-text-fill: " + (isWinner ? "#f1c40f" : "#e74c3c") + ";");

        VBox card = new VBox(7, nameLbl, energyLbl, badge);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: " + (isWinner ? "#1e3044" : "#1a252f") + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + (isWinner ? "#f1c40f" : "#4a6274") + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1.5;"
        );
        return card;
    }

    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
}
