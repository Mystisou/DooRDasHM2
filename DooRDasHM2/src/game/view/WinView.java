package game.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WinView extends VBox {
	
	public WinView(String winnerName, int finalEnergy) {
		this.setSpacing(30);
		this.setAlignment(Pos.CENTER);
		this.setStyle("-fx-background-color: #27ae60;");
		
		Label winlab = new Label("VICTORY!");
		winlab.setStyle("-fx-font-size: 48px; -fx-text-fill: white; -fx-font-weight: bold;");
		
		Label details = new Label(winnerName + " won with " + finalEnergy + " energy!");
		details.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
		
		Button restartBtn = new Button("Return to Main Menu");
		restartBtn.setPrefSize(200, 50);
		restartBtn.setOnAction(e -> ViewManager.updateView(new StartView()));
	}
	
    
	

}
