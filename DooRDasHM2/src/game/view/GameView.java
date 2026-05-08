package game.view;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.Node;

public class GameView extends BorderPane {
	private Button rollBtn;
	private Button powerBtn;
	private GridPane gameBoard;
	private VBox rightPanel;
	private Label player1Stats;
	private Label player2Stats;
	private Label gameLog;

	private Circle playerIcon;
	private Circle opponentIcon;

	public GameView() {
		this.setPadding(new Insets(20));
		this.setStyle("-fx-background-color: #34495e;");

		gameLog = new Label("Welcome to the Floor! Player 1's turn");
		gameLog.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10;");
		this.setTop(gameLog);
		BorderPane.setAlignment(gameLog, Pos.CENTER);

		gameBoard = new GridPane();
		gameBoard.setAlignment(Pos.CENTER);
		initalizeBoard();
		this.setCenter(gameBoard);

		rightPanel = new VBox(20);
		rightPanel.setPadding(new Insets(0,0,0,20));
		rightPanel.setPrefWidth(250);

		Label monsterTitle = new Label("MONSTER STATS");
		monsterTitle.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold; -fx-font-size: 20px;");    	

		player1Stats = new Label("Player: Sulley\nRole: SCARER\nEnergy: 0\nPosition: 0");
		player1Stats.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-background-color: #2c3e50; -fx-padding: 15; -fx-background-radius: 10;");

		player2Stats = new Label("P2: Mike\nEnergy: 0\nPos: 0\nStatus: Active");
		player2Stats.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-background-color: #2c3e50; -fx-padding: 15; -fx-background-radius: 10;");

		rightPanel.getChildren().addAll(monsterTitle, player1Stats, player2Stats);
		this.setRight(rightPanel);

		HBox controlBtns = new HBox(20);
		controlBtns.setAlignment(Pos.CENTER);
		controlBtns.setPadding(new Insets(20,0,0,0));

		rollBtn = new Button("ROLL DICE");
		powerBtn = new Button("ACTIVATE POWER");

		String btnStyle = "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 30;";
		rollBtn.setStyle(btnStyle);
		powerBtn.setStyle(btnStyle.replace("#27ae60", "#e67e22"));

		controlBtns.getChildren().addAll(rollBtn, powerBtn);
		this.setBottom(controlBtns);

		playerIcon = new Circle(12, Color.CYAN);
		opponentIcon = new Circle(12, Color.MAGENTA);


		movePlayer(0, true);
		movePlayer(0, false);
	}


	private void initalizeBoard() {
		for(int i = 0; i<100; i++) {
			StackPane cell = new StackPane();
			cell.setPrefSize(60, 60);

			int row = 9 - (i / 10);
			int col;
			if((i/10)%2 == 0)
				col = i % 10;
			else
				col = 9 - (i%10);

			String color;
			if (((i / 10) + i) % 2 == 0)
				color = "#ecf0f1";
			else 
				color = "#bdc3c7";
			cell.setStyle("-fx-background-color: " + color + "; -fx-border-color: #7f8c8d;");


			Label cellNum = new Label(String.valueOf(i));
			cellNum.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
			StackPane.setAlignment(cellNum, Pos.TOP_LEFT);
			cell.setPadding(new Insets(2));

			cell.getChildren().add(cellNum);
			gameBoard.add(cell, col, row);
		}
	}

	public void movePlayer(int cellIndex, boolean isCurrentPlayer) {
		Circle icon = isCurrentPlayer ? playerIcon : opponentIcon;

		if (icon.getParent() != null) {
			((Pane) icon.getParent()).getChildren().remove(icon);
		}

		int rowCountFromBottom = cellIndex / 10;
		int row = 9 - rowCountFromBottom;
		int col = (rowCountFromBottom % 2 == 0) ? (cellIndex % 10) : (9 - (cellIndex % 10));


		for (Node node : gameBoard.getChildren()) {
			Integer nodeCol = GridPane.getColumnIndex(node);
			Integer nodeRow = GridPane.getRowIndex(node);

			if (nodeCol != null && nodeRow != null && nodeCol == col && nodeRow == row) {
				StackPane targetCell = (StackPane) node;
				targetCell.getChildren().add(icon);
				StackPane.setAlignment(icon, isCurrentPlayer ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
				break;
			}
		}
	}

	public void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public void updateStats(String p1Text, String p2Text) {
		this.player1Stats.setText(p1Text);
		this.player2Stats.setText(p2Text);
	}

	public void updateLog(String message) {
		this.gameLog.setText(message);
	}

	public void setCellColor(int index, String hexColor) {
		int rowCountFromBottom = index / 10;
		int row = 9 - rowCountFromBottom;
		int col;
		if (rowCountFromBottom % 2 == 0)
			col = index % 10;
		else 
			col = 9 - (index % 10);

		for (Node node : gameBoard.getChildren()) {
			Integer nodeCol = GridPane.getColumnIndex(node);
			Integer nodeRow = GridPane.getRowIndex(node);

			if (nodeCol != null && nodeRow != null && nodeCol == col && nodeRow == row) {
				node.setStyle("-fx-background-color: " + hexColor + "; -fx-border-color: #7f8c8d;");
				break; 
			}
		}
	}

	public Button getRollBtn() {
		return rollBtn;
	}


	public Button getPowerBtn() {
		return powerBtn;
	}



}

