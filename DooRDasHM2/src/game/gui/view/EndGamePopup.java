package game.gui.view;

import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public final class EndGamePopup {

    private static final double W = 600;
    private static final double H = 510;

    private static final String BG       = "#0d0d1a";
    private static final String GOLD     = "#f1c40f";
    private static final String RED      = "#e74c3c";
    private static final String DIM      = "#636e72";

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    
    public static void show(
            boolean playerWon,
            String  playerName,   String playerRole,   int playerEnergy,
            String  opponentName, String opponentRole, int opponentEnergy,
            Window  owner,
            Runnable onReturn) {

        Stage popup = new Stage();
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) popup.initOwner(owner);
        popup.setResizable(false);

        
        String titleText  = playerWon ? "YOU WON!" : "YOU LOST!";
        String titleColor = playerWon ? GOLD : RED;

        Label titleLbl = new Label(titleText);
        titleLbl.setFont(font(F_BANGERS, 58));
        titleLbl.setStyle(
            "-fx-text-fill: " + titleColor + ";" +
            "-fx-effect: dropshadow(gaussian," + titleColor + ",10,0.18,0,1);"
        );

        
        String sub = playerWon
            ? "The Floor is yours. Monstropolis will never forget this day."
            : "The Floor shows no mercy. Return, recharge, and try again.";
        Label subLbl = new Label(sub);
        subLbl.setFont(font(F_INTER, 12));
        subLbl.setStyle("-fx-text-fill: #bdc3c7;");
        subLbl.setWrapText(true);
        subLbl.setMaxWidth(480);
        subLbl.setAlignment(Pos.CENTER);
        subLbl.setTextAlignment(TextAlignment.CENTER);

        
        VBox playerCard   = buildCharCard(playerName,   playerRole,   playerEnergy,   playerWon,  true);
        VBox opponentCard = buildCharCard(opponentName, opponentRole, opponentEnergy, !playerWon, false);

        HBox cardsRow = new HBox(24, playerCard, opponentCard);
        cardsRow.setAlignment(Pos.CENTER);

        
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setMaxWidth(480);
        divider.setStyle("-fx-background-color: " +
            (playerWon ? "rgba(241,196,15,0.25)" : "rgba(231,76,60,0.20)") + ";");

        
        String btnBase  = playerWon ? "#b8860b" : "#4a5568";
        String btnHover = playerWon ? GOLD       : "#636e72";

        Button returnBtn = new Button("RETURN TO START");
        returnBtn.setFont(font(F_PIXEL, 8));
        returnBtn.setPrefSize(210, 42);
        applyBtn(returnBtn, btnBase, true);
        returnBtn.setOnMouseEntered(e -> applyBtn(returnBtn, btnHover, true));
        returnBtn.setOnMouseExited(e  -> applyBtn(returnBtn, btnBase,  true));
        returnBtn.setOnAction(e -> { popup.close(); onReturn.run(); });

        
        VBox content = new VBox(10, titleLbl, subLbl, divider, cardsRow, returnBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28, 32, 24, 32));

        Rectangle clip = new Rectangle(W, H);
        clip.setArcWidth(22); clip.setArcHeight(22);

        String borderColor = playerWon ? "rgba(241,196,15,0.60)" : "rgba(231,76,60,0.50)";
        String glowColor   = playerWon ? GOLD : RED;

        StackPane root = new StackPane(content);
        root.setClip(clip);
        root.setStyle(
            "-fx-background-color: " + BG + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian," + glowColor + ",40,0.28,0,0);"
        );

        Scene scene = new Scene(root, W, H);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);

        Rectangle2D sb = Screen.getPrimary().getVisualBounds();
        popup.setX(sb.getMinX() + (sb.getWidth()  - W) / 2);
        popup.setY(sb.getMinY() + (sb.getHeight() - H) / 2);

        popup.showAndWait();
    }

    

    private static VBox buildCharCard(
            String name, String role, int energy,
            boolean isWinner, boolean isYou) {

        String accentColor = isWinner ? GOLD  : DIM;
        String textColor   = isWinner ? "#ecf0f1" : "#7f8c8d";
        String energyColor = isWinner ? "#2ecc71" : DIM;

        
        StackPane photoPane = new StackPane();
        photoPane.setMinSize(86, 86);
        photoPane.setMaxSize(86, 86);
        photoPane.setStyle(
            "-fx-background-color: #1a1a2e;" +
            "-fx-background-radius: 43;" +
            "-fx-border-color: " + accentColor + ";" +
            "-fx-border-width: " + (isWinner ? "3" : "2") + ";" +
            "-fx-border-radius: 43;" +
            (isWinner ? "-fx-effect: dropshadow(gaussian," + GOLD + ",18,0.55,0,0);" : "")
        );

        Image img = monsterImage(name, 80, 80);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(80); iv.setFitHeight(80);
            iv.setClip(new Circle(40, 40, 40));
            photoPane.getChildren().add(iv);

            
            if (!isWinner) {
                Circle dimCircle = new Circle(40);
                dimCircle.setFill(Color.web("#0d0d1a", 0.52));
                photoPane.getChildren().add(dimCircle);
            }
        } else {
            Label fb = new Label(isYou ? "P" : "O");
            fb.setFont(font(F_BANGERS, 30));
            fb.setStyle("-fx-text-fill: " + accentColor + ";");
            photoPane.getChildren().add(fb);
        }

        
        Label badge = new Label(isWinner ? "WINNER" : "LOSER");
        badge.setFont(font(F_PIXEL, 6));
        badge.setStyle(
            "-fx-text-fill: " + (isWinner ? "#0d0d1a" : "#bdc3c7") + ";" +
            "-fx-background-color: " + (isWinner ? GOLD : "#4a5568") + ";" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 3 8;"
        );

        
        Label youLbl = new Label(isYou ? "YOU" : "OPPONENT");
        youLbl.setFont(font(F_BANGERS, 13));
        youLbl.setStyle("-fx-text-fill: " + textColor + ";");

        
        Label nameLbl = new Label(name);
        nameLbl.setFont(font(F_BANGERS, 19));
        nameLbl.setStyle("-fx-text-fill: " + accentColor + ";");
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(190);
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(TextAlignment.CENTER);

        
        Label roleLbl = new Label(role);
        roleLbl.setFont(font(F_INTER, 11));
        roleLbl.setStyle("-fx-text-fill: " + textColor + "; -fx-opacity: 0.85;");

        
        Label energyLbl = new Label(energy + " energy");
        energyLbl.setFont(font(F_BANGERS, 16));
        energyLbl.setStyle("-fx-text-fill: " + energyColor + ";");

        
        VBox card = new VBox(5, photoPane, badge, youLbl, nameLbl, roleLbl, energyLbl);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setPrefWidth(218);
        card.setStyle(
            "-fx-background-color: " +
                (isWinner ? "rgba(241,196,15,0.07)" : "rgba(99,110,114,0.07)") + ";" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + accentColor + (isWinner ? ";" : "77;") +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1.5;"
        );
        return card;
    }

    

    private static void applyBtn(Button btn, String color, boolean dark) {
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: " + (dark ? "#0d0d1a" : "white") + ";" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + color + ",12,0.40,0,0);"
        );
    }

    private static Image monsterImage(String name, double w, double h) {
        if (name.contains("Sullivan"))   return ResourceLoader.loadImage("James_Sullivan",   w, h);
        if (name.contains("Wazowski"))   return ResourceLoader.loadImage("Mike_Wazowski",    w, h);
        if (name.contains("Randall"))    return ResourceLoader.loadImage("Randall_Boggs",    w, h);
        if (name.contains("Celia"))      return ResourceLoader.loadImage("Celia_Mae",        w, h);
        if (name.contains("Waternoose")) return ResourceLoader.loadImage("Henry_Waternoose", w, h);
        if (name.contains("Roz"))        return ResourceLoader.loadImage("Roz",              w, h);
        if (name.contains("Fungus"))     return ResourceLoader.loadImage("Fungus",           w, h);
        if (name.contains("Yeti"))       return ResourceLoader.loadImage("Yeti",             w, h);
        return null;
    }

    private static Font font(String path, double size) { return ResourceLoader.font(path, size); }

    private EndGamePopup() {}
}
