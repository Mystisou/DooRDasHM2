package game.gui.view;

import game.engine.Constants;
import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class GameView extends BorderPane {

    private static final String BG_DARK      = "#0d0d1a";
    private static final String BG_MID       = "#1a1a2e";
    private static final String GOLD         = "#f1c40f";
    private static final String TEXT_MAIN    = "#ecf0f1";
    private static final String TEXT_DIM     = "#95a5a6";
    private static final String RED_BTN      = "#c0392b";
    private static final String RED_HOVER    = "#e74c3c";

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    private Button    powerBtn;
    private ImageView diceImageView;
    private StackPane diceWrapper;
    private Label     diceLabel;

    private GridPane    gameBoard;
    private StackPane[] cells = new StackPane[100];

    private StackPane playerToken;
    private StackPane opponentToken;

    private Label logLabel;

    private Label p1Name, p1OrigRole, p1CurRole, p1Type, p1Energy, p1Pos, p1Status;
    private Label p2Name, p2OrigRole, p2CurRole, p2Type, p2Energy, p2Pos, p2Status;

    private Label p1DeltaLbl;
    private Label p2DeltaLbl;

    private StackPane p1PhotoPane;
    private StackPane p2PhotoPane;

    public GameView() {
        this.setStyle("-fx-background-color: " + BG_DARK + ";");
        this.setPadding(new Insets(10));
        buildTop();
        buildSides();
        buildCentreBoard();
        buildBottom();
    }

    private void buildTop() {
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
        logLabel.setWrapText(true);
        logLabel.setMaxWidth(460);
        logLabel.setAlignment(Pos.CENTER);

        VBox top = new VBox(logLabel);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(4, 0, 10, 0));
        this.setTop(top);
    }

    private void buildSides() {
        VBox p1Card = buildStatCard(true);
        VBox p2Card = buildStatCard(false);
        BorderPane.setMargin(p1Card, new Insets(0, 12, 0, 0));
        BorderPane.setMargin(p2Card, new Insets(0, 0, 0, 12));
        this.setLeft(p1Card);
        this.setRight(p2Card);
    }

    private VBox buildStatCard(boolean isPlayer) {
        String accent   = isPlayer ? "#00bcd4" : "#e91e63";
        String labelTxt = isPlayer ? "YOU"     : "OPPONENT";

        StackPane photoPane = new StackPane();
        photoPane.setMinSize(72, 72);
        photoPane.setPrefSize(72, 72);
        photoPane.setMaxSize(72, 72);
        photoPane.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-background-radius: 36;" +
            "-fx-border-color: " + accent + "BB;" +
            "-fx-border-width: 2.5;" +
            "-fx-border-radius: 36;"
        );
        Label fb = new Label(isPlayer ? "P" : "O");
        fb.setFont(font(F_BANGERS, 28));
        fb.setStyle("-fx-text-fill: " + accent + ";");
        photoPane.getChildren().add(fb);

        if (isPlayer) p1PhotoPane = photoPane;
        else          p2PhotoPane = photoPane;

        Label headerLbl = new Label(labelTxt);
        headerLbl.setFont(font(F_BANGERS, 22));
        headerLbl.setStyle("-fx-text-fill: white;");

        VBox topRow = new VBox(5, photoPane, headerLbl);
        topRow.setAlignment(Pos.CENTER);
        topRow.setPadding(new Insets(0, 0, 4, 0));

        Separator sep = new Separator();

        Label nm  = statLbl(TEXT_MAIN, 13, true);
        Label or_ = statLbl(TEXT_DIM,  12, false);
        Label cr  = statLbl(TEXT_DIM,  12, false);
        Label tp  = statLbl(TEXT_DIM,  12, false);
        Label en  = statLbl("#2ecc71", 13, true);
        Label ps  = statLbl("#3498db", 12, false);
        Label st  = statLbl("#e67e22", 12, false);

        nm .setText("—");
        or_.setText("Role:   —");
        cr .setText("Active: —");
        tp .setText("Type:   —");
        en .setText("Energy: —");
        ps .setText("Cell:   0");
        st .setText("Status: Normal");

        if (isPlayer) {
            p1Name=nm; p1OrigRole=or_; p1CurRole=cr;
            p1Type=tp; p1Energy=en;   p1Pos=ps; p1Status=st;
        } else {
            p2Name=nm; p2OrigRole=or_; p2CurRole=cr;
            p2Type=tp; p2Energy=en;   p2Pos=ps; p2Status=st;
        }

        Label deltaLbl = new Label("");
        deltaLbl.setFont(font(F_INTER, 12));
        deltaLbl.setAlignment(Pos.CENTER);
        deltaLbl.setMaxWidth(Double.MAX_VALUE);
        if (isPlayer) p1DeltaLbl = deltaLbl;
        else          p2DeltaLbl = deltaLbl;

        VBox card = new VBox(6, topRow, sep, nm, or_, cr, tp, en, deltaLbl, ps, st);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setPrefWidth(218);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: " + accent + "1C;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + accent + "BB;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1.5;" +
            "-fx-effect: dropshadow(gaussian," + accent + ",12,0.25,0,0);"
        );
        return card;
    }

    private Label statLbl(String colour, int size, boolean bold) {
        Label l = new Label();
        l.setFont(font(F_INTER, size));
        l.setStyle("-fx-text-fill: " + colour + ";" + (bold ? " -fx-font-weight: bold;" : ""));
        l.setAlignment(Pos.CENTER);
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private void buildCentreBoard() {
        gameBoard = new GridPane();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setHgap(1);
        gameBoard.setVgap(1);
        gameBoard.setPadding(new Insets(6));
        gameBoard.setStyle(
            "-fx-background-color: rgba(108,52,131,0.13);" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(155,89,182,0.40);" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1.5;" +
            "-fx-effect: dropshadow(gaussian,#6c3483,14,0.20,0,0);"
        );

        playerToken   = makeToken("P", Color.CYAN);
        opponentToken = makeToken("O", Color.MAGENTA);

        for (int i = 0; i < 100; i++) {
            cells[i] = buildCell(i);
            int row = 9 - (i / 10);
            int col = (i / 10) % 2 == 0 ? (i % 10) : (9 - (i % 10));
            gameBoard.add(cells[i], col, row);
        }
        this.setCenter(gameBoard);
    }

    private StackPane buildCell(int i) {
        StackPane cell = new StackPane();
        cell.setPrefSize(53, 53);
        cell.setMinSize(53, 53);
        cell.setMaxSize(53, 53);

        ImageView bg = imageView(cellImageName(i), 53, 53);
        if (bg != null) {
            cell.getChildren().add(bg);
        } else {
            cell.setStyle("-fx-background-color: " + fallbackColour(i)
                + "; -fx-border-color: rgba(0,0,0,0.35); -fx-border-width: 0.5;");
        }

        Label numLbl = new Label(String.valueOf(i));
        numLbl.setFont(font(F_INTER, 11));
        numLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        StackPane.setAlignment(numLbl, Pos.TOP_LEFT);
        StackPane.setMargin(numLbl, new Insets(2, 0, 0, 3));
        cell.getChildren().add(numLbl);

        if (i == 0)  addCellBadge(cell, "START", "#16a085");
        if (i == 99) addCellBadge(cell, "END",   "#f39c12");
        return cell;
    }

    private void addCellBadge(StackPane cell, String text, String bg) {
        Label l = new Label(text);
        l.setFont(font(F_PIXEL, 7));
        l.setStyle("-fx-text-fill: white; -fx-background-color: " + bg
            + "; -fx-background-radius: 4; -fx-padding: 2 4;");
        StackPane.setAlignment(l, Pos.CENTER);
        cell.getChildren().add(l);
    }

    private StackPane makeToken(String letter, Color colour) {
        Circle c = new Circle(13);
        c.setFill(colour);
        c.setStroke(Color.WHITE);
        c.setStrokeWidth(2);
        c.setEffect(new DropShadow(10, colour));

        Label l = new Label(letter);
        l.setFont(font(F_BANGERS, 14));
        l.setStyle("-fx-text-fill: " + BG_DARK + ";");

        StackPane token = new StackPane(c, l);
        token.setMaxSize(30, 30);
        return token;
    }

    private void buildBottom() {
        powerBtn = new Button("ACTIVATE POWER");
        powerBtn.setFont(font(F_PIXEL, 8));
        powerBtn.setPrefSize(196, 48);
        applyBtnStyle(powerBtn, RED_BTN);
        powerBtn.setOnMouseEntered(e -> applyBtnStyle(powerBtn, RED_HOVER));
        powerBtn.setOnMouseExited(e  -> applyBtnStyle(powerBtn, RED_BTN));

        ImageView rawDice = imageView("dice", 72, 72);
        diceImageView = (rawDice != null) ? rawDice : new ImageView();
        diceImageView.setFitWidth(72);
        diceImageView.setFitHeight(72);
        diceImageView.setClip(new Circle(36, 36, 36));
        diceImageView.setStyle("-fx-cursor: hand;");

        diceWrapper = new StackPane(diceImageView);
        diceWrapper.setMinSize(76, 76);
        diceWrapper.setMaxSize(76, 76);
        diceWrapper.setStyle("-fx-cursor: hand;");
        setDiceTurnIndicator(true);

        diceLabel = new Label("Roll to start!");
        diceLabel.setFont(font(F_INTER, 16));
        diceLabel.setMinWidth(215);
        diceLabel.setAlignment(Pos.CENTER);
        diceLabel.setStyle(
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-background-color: rgba(108,52,131,0.18);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(155,89,182,0.50);" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.5;" +
            "-fx-padding: 8 18;"
        );

        HBox bottom = new HBox(26, powerBtn, diceLabel, diceWrapper);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(12, 0, 8, 0));
        bottom.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-border-color: rgba(155,89,182,0.30) transparent transparent transparent;" +
            "-fx-border-width: 1.5;"
        );
        this.setBottom(bottom);
    }

    private void applyBtnStyle(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + color + ",14,0.5,0,0);"
        );
    }

    public void movePlayer(int newPos, boolean isCurrentPlayer) {
        StackPane token = isCurrentPlayer ? playerToken : opponentToken;
        if (token.getParent() instanceof StackPane) {
            ((StackPane) token.getParent()).getChildren().remove(token);
        }
        if (newPos >= 0 && newPos < 100) {
            cells[newPos].getChildren().add(token);
            StackPane.setAlignment(token, isCurrentPlayer ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        }
    }

    public void updateMonsterCard(boolean isPlayer,
            String name, String origRole, String curRole,
            String type, int energy, int position, String status) {
        Label nm  = isPlayer ? p1Name     : p2Name;
        Label or_ = isPlayer ? p1OrigRole : p2OrigRole;
        Label cr  = isPlayer ? p1CurRole  : p2CurRole;
        Label tp  = isPlayer ? p1Type     : p2Type;
        Label en  = isPlayer ? p1Energy   : p2Energy;
        Label ps  = isPlayer ? p1Pos      : p2Pos;
        Label st  = isPlayer ? p1Status   : p2Status;

        boolean confused = !origRole.equals(curRole);
        (isPlayer ? p1DeltaLbl : p2DeltaLbl).setText("");
        nm .setText(name);
        or_.setText("Role:   " + origRole);
        cr .setText("Active: " + curRole + (confused ? "  CONFUSED" : ""));
        cr .setStyle("-fx-text-fill: " + (confused ? "#e74c3c" : TEXT_DIM) + "; -fx-font-size: 12px;");
        tp .setText("Type:   " + type);
        en .setText("Energy: " + energy);
        ps .setText("Cell:   " + position);
        st .setText("Status: " + status);
    }

    public void setPlayerPhoto(boolean isPlayer, String monsterName, String role) {
        StackPane pane   = isPlayer ? p1PhotoPane : p2PhotoPane;
        String    accent = isPlayer ? "#00bcd4"   : "#e91e63";

        Image img = monsterImage(monsterName, 68, 68);
        pane.getChildren().clear();
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(68);
            iv.setFitHeight(68);
            iv.setPreserveRatio(false);
            iv.setClip(new Circle(34, 34, 34));
            iv.setEffect(new DropShadow(14, Color.web(accent, 0.55)));
            pane.getChildren().add(iv);
        } else {
            Label fb = new Label(isPlayer ? "P" : "O");
            fb.setFont(font(F_BANGERS, 28));
            fb.setStyle("-fx-text-fill: " + accent + ";");
            pane.getChildren().add(fb);
        }
    }

    public void updateLog(String message) { logLabel.setText(message); }

    public void setDiceTurnIndicator(boolean isPlayerTurn) {
        DropShadow glow = new DropShadow(26, isPlayerTurn ? Color.CYAN : Color.RED);
        glow.setSpread(0.40);
        diceWrapper.setEffect(glow);
        diceWrapper.setOpacity(isPlayerTurn ? 1.0 : 0.65);
    }

    public void setDoorEnergyLabel(int index, int energy) {
        if (index < 0 || index >= 100) return;
        Label enLbl = new Label(String.valueOf(energy));
        enLbl.setFont(font(F_INTER, 11));
        enLbl.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold;");
        StackPane.setAlignment(enLbl, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(enLbl, new Insets(0, 3, 2, 0));
        cells[index].getChildren().add(enLbl);
    }

    public void markDoorExhausted(int index) {
        if (index < 0 || index >= 100) return;
        StackPane cell = cells[index];
        if (!cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof ImageView) {
            cell.getChildren().remove(0);
        }
        ImageView ex = imageView("exhausted_door", 53, 53);
        if (ex != null) cell.getChildren().add(0, ex);
        else            cell.setStyle("-fx-background-color: #4a4a4a; -fx-opacity: 0.55;");
    }

    public void showDiceResult(int roll) {
        String[] faces = {"", "⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};
        String face = (roll >= 1 && roll <= 6) ? faces[roll] : "?";
        diceLabel.setText(face + "  Rolled: " + roll);
    }

    public void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    public void showEnergyDelta(boolean isPlayer, int delta) {
        Label lbl = isPlayer ? p1DeltaLbl : p2DeltaLbl;
        if (delta > 0) {
            lbl.setText("▲  +" + delta + " energy");
            lbl.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        } else if (delta < 0) {
            lbl.setText("▼  " + delta + " energy");
            lbl.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else {
            lbl.setText("");
        }
    }

    public void showCellEffect(String message) {
        updateLog(message);
    }

    public void showCardDrawn(String cardName, String cardEffect) {
        javafx.stage.Stage popup = new javafx.stage.Stage();
        popup.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        popup.setResizable(false);

        Label icon = new Label("🃏");
        icon.setStyle("-fx-font-size: 40px;");

        Label nameLbl = new Label(cardName);
        nameLbl.setFont(font(F_BANGERS, 28));
        nameLbl.setStyle("-fx-text-fill: " + GOLD + ";");

        Label effectLbl = new Label(cardEffect);
        effectLbl.setFont(font(F_INTER, 13));
        effectLbl.setStyle("-fx-text-fill: #ecf0f1;");
        effectLbl.setWrapText(true);
        effectLbl.setMaxWidth(320);
        effectLbl.setAlignment(javafx.geometry.Pos.CENTER);
        effectLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        javafx.scene.control.Button okBtn = new javafx.scene.control.Button("GOT IT");
        okBtn.setFont(font(F_PIXEL, 10));
        okBtn.setPrefSize(150, 40);
        applyBtnStyle(okBtn, RED_BTN);
        okBtn.setOnMouseEntered(e -> applyBtnStyle(okBtn, RED_HOVER));
        okBtn.setOnMouseExited(e  -> applyBtnStyle(okBtn, RED_BTN));
        okBtn.setOnAction(e -> popup.close());

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(14, icon, nameLbl, effectLbl, okBtn);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setPadding(new javafx.geometry.Insets(30));

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(content);
        root.setStyle(
            "-fx-background-color: #0d0d1a;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(155,89,182,0.60);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 2;"
        );

        javafx.scene.Scene scene = new javafx.scene.Scene(root, 380, 260);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    public Button    getPowerBtn()      { return powerBtn;      }
    public ImageView getDiceImageView() { return diceImageView; }

    private Image monsterImage(String name, double w, double h) {
        if (name.contains("Sullivan"))   return ResourceLoader.loadImage("James_Sullivan",    w, h);
        if (name.contains("Wazowski"))   return ResourceLoader.loadImage("Mike_Wazowski",     w, h);
        if (name.contains("Randall"))    return ResourceLoader.loadImage("Randall_Boggs",     w, h);
        if (name.contains("Celia"))      return ResourceLoader.loadImage("Celia_Mae",         w, h);
        if (name.contains("Waternoose")) return ResourceLoader.loadImage("Henry_Waternoose",  w, h);
        if (name.contains("Roz"))        return ResourceLoader.loadImage("Roz",               w, h);
        if (name.contains("Fungus"))     return ResourceLoader.loadImage("Fungus",            w, h);
        if (name.contains("Yeti"))       return ResourceLoader.loadImage("Yeti",              w, h);
        return null;
    }

    private ImageView imageView(String name, double w, double h) {
        String key = "energy".equals(name) ? "card_cell" : name;
        Image img = ResourceLoader.loadImage(key, w, h);
        if (img == null) return null;
        ImageView iv = new ImageView(img);
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(false);
        return iv;
    }

    private String cellImageName(int i) {
        if (has(Constants.CONVEYOR_CELL_INDICES, i)) return "conveyor";
        if (has(Constants.SOCK_CELL_INDICES,     i)) return "sock";
        if (has(Constants.CARD_CELL_INDICES,     i)) return "energy";
        if (has(Constants.MONSTER_CELL_INDICES,  i)) return "monster_cell";
        if (i % 2 == 1) return (i / 2) % 2 == 0 ? "scarer_door" : "laugher_door";
        return "metallic_hud_texture";
    }

    private String fallbackColour(int i) {
        if (i == 0)  return "#16a085";
        if (i == 99) return "#f39c12";
        if (has(Constants.CONVEYOR_CELL_INDICES, i)) return "#27ae60";
        if (has(Constants.SOCK_CELL_INDICES,     i)) return "#d35400";
        if (has(Constants.CARD_CELL_INDICES,     i)) return "#c0392b";
        if (has(Constants.MONSTER_CELL_INDICES,  i)) return "#8e44ad";
        if (i % 2 == 1) return (i / 2) % 2 == 0 ? "#2980b9" : "#1abc9c";
        int row = i / 10, col = i % 10;
        return (row + col) % 2 == 0 ? "#2c3e50" : "#34495e";
    }

    private Font font(String path, double size) {
        return ResourceLoader.font(path, size);
    }

    private boolean has(int[] arr, int val) {
        for (int v : arr) if (v == val) return true;
        return false;
    }
}
