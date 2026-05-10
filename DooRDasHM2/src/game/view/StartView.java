package game.view;

//import game.controller.Controller;
import game.engine.Game;
import game.engine.Role;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StartView extends VBox {

    public StartView() {
        this.setSpacing(25);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #2c3e50; -fx-padding: 50;");

        
        Label title = new Label("DooR DasH: Scare vs Laugh");
        title.setStyle("-fx-font-size: 32px; -fx-text-fill: white; -fx-font-weight: bold;");

        
        TextArea instructions = new TextArea("HOW TO PLAY:\n"
                + "1. Select your monster's role.\n"
                + "2. Navigate the 100-cell grid.\n"
                + "3. Reach cell 99 with at least 1000 energy to win!\n"
                + "4. Watch out for contamination socks.");
        instructions.setEditable(false);
        instructions.setWrapText(true);
        instructions.setMaxWidth(600);
        instructions.setPrefHeight(150);

        
        Label choose = new Label("Choose Your Side:");
        choose.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        RadioButton scarer = new RadioButton("SCARER");
        RadioButton laugher = new RadioButton("LAUGHER");
        scarer.setStyle("-fx-text-fill: white;");
        laugher.setStyle("-fx-text-fill: white;");

        ToggleGroup roleGroup = new ToggleGroup();
        scarer.setToggleGroup(roleGroup);
        laugher.setToggleGroup(roleGroup);
        scarer.setSelected(true);

        HBox roleBox = new HBox(40, scarer, laugher);
        roleBox.setAlignment(Pos.CENTER);

        
        Button startBtn = new Button("ENTER THE FLOOR");
        startBtn.setPrefSize(200, 50);
        startBtn.setOnAction(e -> {
            try {
                Role startingRole = scarer.isSelected() ? Role.SCARER : Role.LAUGHER;
                Game newGame = new Game(startingRole);
                GameView gameScreen = new GameView();
                Controller controller = new Controller(newGame, gameScreen);
                ViewManager.updateView(gameScreen);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Loading Error");
                alert.setContentText("Could not load the game files. Check your CSVs!");
                alert.showAndWait();
            }
        });

        this.getChildren().addAll(title, instructions, choose, roleBox, startBtn);
    }
}