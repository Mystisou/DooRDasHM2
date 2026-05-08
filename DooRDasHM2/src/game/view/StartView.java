package game.view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class StartView extends VBox {

    public StartView() {
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 40;");

        Label title = new Label("DooR DasH: Scarer vs Laugher");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        TextArea instructions = new TextArea("HOW TO PLAY:\n1. Choose your role.\n2. Reach cell 99 with 1000 energy to win!\n3. Avoid the socks!");
        instructions.setEditable(false);
        instructions.setWrapText(true);
        instructions.setMaxWidth(500);
        instructions.setPrefHeight(120);

        Label roleLabel = new Label("Select Your Side:");
        RadioButton scarer = new RadioButton("SCARER");
        RadioButton laugher = new RadioButton("LAUGHER");
        
        ToggleGroup group = new ToggleGroup();
        scarer.setToggleGroup(group);
        laugher.setToggleGroup(group);
        scarer.setSelected(true); 

        HBox roles = new HBox(30, scarer, laugher);
        roles.setAlignment(Pos.CENTER);

        Button startBtn = new Button("START GAME");
        startBtn.setPrefSize(150, 50);

        startBtn.setOnAction(e -> {
            String selectedRole = scarer.isSelected() ? "SCARER" : "LAUGHER";
            System.out.println("Role selected: " + selectedRole);
            
            // Logic to transition to the Game Board goes here later
            // For now, let's just show a simple "Game Started" label
            
            ViewManager.updateView(new StackPane(new Label("Game Board Loading...")));
        });

        this.getChildren().addAll(title, instructions, roleLabel, roles, startBtn);
    }
}