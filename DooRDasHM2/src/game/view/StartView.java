package game.view;

import game.controller.GameController;
import game.engine.Game;
import game.engine.Role;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * VIEW — the welcome / role-selection screen shown at launch.
 */
public class StartView extends VBox {

    public StartView() {
        this.setSpacing(25);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #1a252f; -fx-padding: 50;");

        // ── Title ──────────────────────────────────────────────────────
        Label title = new Label("DooR DasH: Scare vs Laugh Touchdown");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #f1c40f; -fx-font-weight: bold;");
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);

        Label subtitle = new Label("Who will collect 1000 energy and reach Boo's Door first?");
        subtitle.setStyle("-fx-font-size: 15px; -fx-text-fill: #bdc3c7;");

        // ── Instructions ───────────────────────────────────────────────
        TextArea instructions = new TextArea(
            "HOW TO PLAY\n" +
            "─────────────────────────────\n" +
            "• Select your side: SCARER or LAUGHER.\n" +
            "• On your turn, optionally activate your power (costs 500 energy),\n" +
            "  then roll the dice to move forward.\n" +
            "• Land on Doors to gain (or lose) energy.\n" +
            "• Conveyor Belts move you forward; Contamination Socks drag you back.\n" +
            "• Card Cells draw a mystery card — could help or hurt!\n" +
            "• Monster Cells: same role = free power-up; opposite = energy swap.\n" +
            "• First to reach cell 99 with ≥ 1000 energy wins!"
        );
        instructions.setEditable(false);
        instructions.setWrapText(true);
        instructions.setMaxWidth(640);
        instructions.setPrefHeight(170);
        instructions.setStyle("-fx-font-size: 13px; -fx-control-inner-background: #1e3044; " +
                              "-fx-text-fill: #ecf0f1; -fx-background-radius: 8;");

        // ── Role selection ─────────────────────────────────────────────
        Label chooseLbl = new Label("Choose Your Side:");
        chooseLbl.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");

        RadioButton scarerBtn  = new RadioButton("😱  SCARER");
        RadioButton laugherBtn = new RadioButton("😂  LAUGHER");
        scarerBtn .setStyle("-fx-text-fill: #3498db; -fx-font-size: 15px;");
        laugherBtn.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 15px;");

        ToggleGroup roleGroup = new ToggleGroup();
        scarerBtn .setToggleGroup(roleGroup);
        laugherBtn.setToggleGroup(roleGroup);
        scarerBtn.setSelected(true);

        HBox roleRow = new HBox(50, scarerBtn, laugherBtn);
        roleRow.setAlignment(Pos.CENTER);

        // ── Start button ────────────────────────────────────────────────
        Button startBtn = new Button("⚔  ENTER THE FLOOR");
        startBtn.setPrefSize(240, 54);
        startBtn.setStyle(
            "-fx-background-color: #e74c3c; -fx-text-fill: white;" +
            "-fx-font-size: 17px; -fx-font-weight: bold;" +
            "-fx-background-radius: 10; -fx-cursor: hand;"
        );

        Label errorLbl = new Label();
        errorLbl.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");

        startBtn.setOnAction(e -> {
            String selected = scarerBtn.isSelected() ? "SCARER" : "LAUGHER";
            try {
                // 1. Build the Model
                Game game = new Game(Role.valueOf(selected));

                // 2. Build the View
                GameView gameView = new GameView();

                // 3. Wire them with the Controller
                //    (the constructor attaches button handlers and paints the initial board)
                new GameController(game, gameView);

                // 4. Switch to the game screen
                ViewManager.updateView(gameView);

            } catch (Exception ex) {
                errorLbl.setText("⚠ Failed to load game: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        this.getChildren().addAll(title, subtitle, instructions, chooseLbl, roleRow, startBtn, errorLbl);
    }
}
