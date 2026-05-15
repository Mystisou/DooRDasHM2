package game.gui.view;

	import game.gui.ResourceLoader;
	import javafx.geometry.Insets;
	import javafx.geometry.Pos;
	import javafx.scene.control.Button;
	import javafx.scene.control.Label;
	import javafx.scene.control.Separator;
	import javafx.scene.layout.HBox;
	import javafx.scene.layout.VBox;
	import javafx.scene.text.Font;

	public class LossView extends VBox {

	    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
	    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
	    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

	    /**
	     * @param loserName    the human player's name
	     * @param loserRole    the human player's role (SCARER / LAUGHER)
	     * @param loserEnergy  the human player's final energy
	     * @param winnerName   the opponent's name
	     * @param winnerEnergy the opponent's final energy
	     */
	    public LossView(String loserName, String loserRole, int loserEnergy,
	                    String winnerName, String winnerRole, int winnerEnergy) {

	        this.setAlignment(Pos.CENTER);
	        this.setSpacing(22);
	        this.setStyle("-fx-background-color: #1a0a0a;");
	        this.setPadding(new Insets(60));

	        Label skull = new Label("💀");
	        skull.setStyle("-fx-font-size: 72px;");

	        Label headline = new Label("YOU LOSE!");
	        headline.setFont(font(F_BANGERS, 52));
	        headline.setStyle("-fx-text-fill: #e74c3c;");

	        Label subLbl = new Label(loserName + " has been defeated.");
	        subLbl.setFont(font(F_INTER, 14));
	        subLbl.setStyle("-fx-text-fill: #bdc3c7;");

	        Label roleLbl = new Label("Role: " + loserRole + "  |  Final Energy: " + loserEnergy);
	        roleLbl.setFont(font(F_INTER, 13));
	        roleLbl.setStyle("-fx-text-fill: #7f8c8d;");

	        String roleColour = winnerRole.equals("SCARER") ? "#3498db" : "#2ecc71";
        Label winnerAnnounceLbl = new Label(winnerName + " WINS!  |  Role: " + winnerRole);
        winnerAnnounceLbl.setFont(font(F_BANGERS, 28));
        winnerAnnounceLbl.setStyle("-fx-text-fill: " + roleColour + ";");

        HBox cardsRow = new HBox(50,
	            buildResultCard(winnerName, winnerEnergy, true),
	            buildResultCard(loserName,  loserEnergy,  false));
	        cardsRow.setAlignment(Pos.CENTER);

	        Button returnBtn = new Button("↩  Return to Start");
	        returnBtn.setFont(font(F_PIXEL, 10));
	        returnBtn.setPrefSize(240, 48);
	        returnBtn.setStyle(
	            "-fx-background-color: #c0392b; -fx-text-fill: white;" +
	            "-fx-background-radius: 10; -fx-cursor: hand;" +
	            "-fx-effect: dropshadow(gaussian,#c0392b,14,0.5,0,0);"
	        );
	        returnBtn.setOnAction(e -> ViewManager.updateView(new StartView()));

	        this.getChildren().addAll(skull, headline, new Separator(), subLbl, roleLbl, winnerAnnounceLbl, cardsRow, returnBtn);
	    }

	    private VBox buildResultCard(String name, int energy, boolean isWinner) {
	        Label nameLbl = new Label(name);
	        nameLbl.setFont(font(F_BANGERS, 22));
	        nameLbl.setStyle("-fx-text-fill: " + (isWinner ? "#f1c40f" : "#e74c3c") + ";");

	        Label energyLbl = new Label("⚡ " + energy + " energy");
	        energyLbl.setFont(font(F_INTER, 13));
	        energyLbl.setStyle("-fx-text-fill: " + (isWinner ? "#2ecc71" : "#e74c3c") + ";");

	        Label badge = new Label(isWinner ? "🏆 WINNER" : "💀 DEFEATED");
	        badge.setFont(font(F_INTER, 12));
	        badge.setStyle("-fx-text-fill: " + (isWinner ? "#f1c40f" : "#e74c3c") + ";");

	        VBox card = new VBox(7, nameLbl, energyLbl, badge);
	        card.setAlignment(Pos.CENTER);
	        card.setPadding(new Insets(16));
	        card.setStyle(
	            "-fx-background-color: " + (isWinner ? "#1e3044" : "#2c1010") + ";" +
	            "-fx-background-radius: 12;" +
	            "-fx-border-color: " + (isWinner ? "#f1c40f" : "#e74c3c") + ";" +
	            "-fx-border-radius: 12;" +
	            "-fx-border-width: 1.5;"
	        );
	        return card;
	    }

	    private Font font(String path, double size) {
	        return ResourceLoader.font(path, size);
	    }
	}