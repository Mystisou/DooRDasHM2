package game.gui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * VIEW — Game-over screen shown when a player wins.
 * Displays winner info, final energies, and a "Return to Start" button.
 */
public class WinView extends VBox {

    public WinView(String winnerName, String winnerRole, int winnerEnergy,
                   String loserName,  int loserEnergy) {

        this.setAlignment(Pos.CENTER);
        this.setSpacing(22);
        this.setStyle("-fx-background-color: #1a252f;");
        this.setPadding(new Insets(60));

        // Trophy + headline
        Label trophy = new Label("🏆");
        trophy.setStyle("-fx-font-size: 72px;");

        Label headline = new Label("GAME OVER!");
        headline.setStyle("-fx-font-size: 38px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");

        // Winner announcement
        String roleColour = winnerRole.equals("SCARER") ? "#3498db" : "#2ecc71";
        Label winnerLbl = new Label(winnerName + " WINS!");
        winnerLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + roleColour + ";");

        Label roleLbl = new Label("Role: " + winnerRole + "  |  Final Energy: " + winnerEnergy);
        roleLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #bdc3c7;");

        // Side-by-side final-energy cards
        HBox cardsRow = new HBox(50, buildResultCard(winnerName, winnerEnergy, true),
                                     buildResultCard(loserName,  loserEnergy,  false));
        cardsRow.setAlignment(Pos.CENTER);

        // Return button
        Button returnBtn = new Button("↩  Return to Start");
        returnBtn.setPrefSize(200, 48);
        returnBtn.setStyle(
            "-fx-background-color: #3498db; -fx-text-fill: white;" +
            "-fx-font-size: 15px; -fx-font-weight: bold;" +
            "-fx-background-radius: 10; -fx-cursor: hand;"
        );
        returnBtn.setOnAction(e -> ViewManager.updateView(new StartView()));

        this.getChildren().addAll(trophy, headline, new Separator(), winnerLbl, roleLbl, cardsRow, returnBtn);
    }

    private VBox buildResultCard(String name, int energy, boolean isWinner) {
        Label nameLbl   = new Label(name);
        nameLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: "
                + (isWinner ? "#f1c40f" : "#bdc3c7") + ";");

        Label energyLbl = new Label("⚡ " + energy + " energy");
        energyLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #2ecc71;");

        Label badge = new Label(isWinner ? "🏆 WINNER" : "💀 DEFEATED");
        badge.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (isWinner ? "#f1c40f" : "#e74c3c") + ";");

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
}
