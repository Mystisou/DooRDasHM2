package game.gui.view;

import game.engine.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.InputStream;

/**
 * VIEW — Game screen.
 * GameController calls the public methods to push model state onto the UI.
 *
 * Theme mirrors StartView (palette, fonts, component patterns).
 * Monster photos loaded from /resources/images/<MonsterKey>.png
 *   where MonsterKey ∈ { James_Sullivan, Mike_Wazowski, Randall_Boggs,
 *                        Celia_Mae, Henry_Waternoose, Roz, Fungus, Yeti }
 *
 * Layout:
 *   TOP    : turn-indicator pill  (Bangers, gold)
 *   LEFT   : YOU stat card        (cyan)
 *   RIGHT  : OPPONENT stat card   (magenta)
 *   CENTRE : game board           (PNG cells, colour fallback)
 *   BOTTOM : Power button | roll-result label | round dice image
 */
public class GameView extends BorderPane {

    // ── Palette ───────────────────────────────────────────────────────
    private static final String BG_DARK      = "#0d0d1a";
    private static final String BG_MID       = "#1a1a2e";
    private static final String PURPLE_LIGHT = "#9b59b6";
    private static final String GOLD         = "#f1c40f";
    private static final String TEXT_MAIN    = "#ecf0f1";
    private static final String TEXT_DIM     = "#95a5a6";
    private static final String RED_BTN      = "#c0392b";
    private static final String RED_HOVER    = "#e74c3c";

    // ── Font paths ────────────────────────────────────────────────────
    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-Regular.ttf";

    // ── Controls ──────────────────────────────────────────────────────
    private Button    powerBtn;
    private ImageView diceImageView;
    private StackPane diceWrapper;    // glow applied here so it stays round
    private Label     diceLabel;

    // ── Board ─────────────────────────────────────────────────────────
    private GridPane    gameBoard;
    private StackPane[] cells = new StackPane[100];

    // ── Tokens ────────────────────────────────────────────────────────
    private StackPane playerToken;
    private StackPane opponentToken;

    // ── Turn label ────────────────────────────────────────────────────
    private Label logLabel;

    // ── Stat-card labels ──────────────────────────────────────────────
    private Label p1Name, p1OrigRole, p1CurRole, p1Type, p1Energy, p1Pos, p1Status;
    private Label p2Name, p2OrigRole, p2CurRole, p2Type, p2Energy, p2Pos, p2Status;

    // ── Photo panes ───────────────────────────────────────────────────
    private StackPane p1PhotoPane;
    private StackPane p2PhotoPane;

    // ─────────────────────────────────────────────────────────────────
    public GameView() {
        this.setStyle("-fx-background-color: " + BG_DARK + ";");
        this.setPadding(new Insets(10));
        buildTop();
        buildSides();
        buildCentreBoard();
        buildBottom();
    }

    // ═══════════════════════════ TOP ═════════════════════════════════

    private void buildTop() {
        logLabel = new Label("YOUR TURN!");
        logLabel.setFont(loadFont(F_BANGERS, 26));
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

    // ═══════════════════════════ SIDES ═══════════════════════════════

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

        /* ── Circular photo pane — filled by setPlayerPhoto() at runtime ── */
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
        // Letter placeholder until setPlayerPhoto() is called
        Label fb = new Label(isPlayer ? "P" : "O");
        fb.setFont(loadFont(F_BANGERS, 28));
        fb.setStyle("-fx-text-fill: " + accent + ";");
        photoPane.getChildren().add(fb);

        if (isPlayer) p1PhotoPane = photoPane;
        else          p2PhotoPane = photoPane;

        /* ── Card header — Bangers, white ── */
        Label headerLbl = new Label(labelTxt);
        headerLbl.setFont(loadFont(F_BANGERS, 22));
        headerLbl.setStyle("-fx-text-fill: white;");

        VBox topRow = new VBox(5, photoPane, headerLbl);
        topRow.setAlignment(Pos.CENTER);
        topRow.setPadding(new Insets(0, 0, 4, 0));

        Separator sep = new Separator();

        /* ── Stat labels ── */
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

        VBox card = new VBox(6, topRow, sep, nm, or_, cr, tp, en, ps, st);
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
        l.setFont(loadFont(F_INTER, size));
        l.setStyle("-fx-text-fill: " + colour + ";" + (bold ? " -fx-font-weight: bold;" : ""));
        l.setAlignment(Pos.CENTER);
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    // ═══════════════════════════ BOARD ═══════════════════════════════

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

        ImageView bg = safeImage(cellImageName(i), 53, 53);
        if (bg != null) {
            cell.getChildren().add(bg);
        } else {
            cell.setStyle("-fx-background-color: " + fallbackColour(i)
                + "; -fx-border-color: rgba(0,0,0,0.35); -fx-border-width: 0.5;");
        }

        // Cell number — white, Inter 11 px, top-left
        Label numLbl = new Label(String.valueOf(i));
        numLbl.setFont(loadFont(F_INTER, 11));
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
        l.setFont(loadFont(F_PIXEL, 7));
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
        l.setFont(loadFont(F_BANGERS, 14));
        l.setStyle("-fx-text-fill: " + BG_DARK + ";");

        StackPane token = new StackPane(c, l);
        token.setMaxSize(30, 30);
        return token;
    }

    // ═══════════════════════════ BOTTOM ══════════════════════════════

    private void buildBottom() {
        /* Power button */
        powerBtn = new Button("ACTIVATE POWER");
        powerBtn.setFont(loadFont(F_PIXEL, 8));
        powerBtn.setPrefSize(196, 48);
        applyBtnStyle(powerBtn, RED_BTN);
        powerBtn.setOnMouseEntered(e -> applyBtnStyle(powerBtn, RED_HOVER));
        powerBtn.setOnMouseExited(e  -> applyBtnStyle(powerBtn, RED_BTN));

        /* Dice — circular clip so only the round part shows */
        ImageView rawDice = safeImage("dice", 72, 72);
        diceImageView = (rawDice != null) ? rawDice : new ImageView();
        diceImageView.setFitWidth(72);
        diceImageView.setFitHeight(72);
        diceImageView.setClip(new Circle(36, 36, 36));  // circular clip
        diceImageView.setStyle("-fx-cursor: hand;");

        /* Wrapper so the glow follows the round shape */
        diceWrapper = new StackPane(diceImageView);
        diceWrapper.setMinSize(76, 76);
        diceWrapper.setMaxSize(76, 76);
        diceWrapper.setStyle("-fx-cursor: hand;");
        setDiceTurnIndicator(true);

        /* Roll-result label */
        diceLabel = new Label("Roll to start!");
        diceLabel.setFont(loadFont(F_INTER, 16));
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

    // ═══════════════════ PUBLIC API ═══════════════════════════════════

    /**
     * Moves a player token to a board cell (O(1) — cells[] direct lookup).
     * @param newPos          0–99
     * @param isCurrentPlayer true = human (cyan), false = opponent (magenta)
     */
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

    /**
     * Refreshes all fields in one stat card.
     * "Status: " is prepended automatically.
     */
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
        nm .setText(name);
        or_.setText("Role:   " + origRole);
        cr .setText("Active: " + curRole + (confused ? "  CONFUSED" : ""));
        cr .setStyle("-fx-text-fill: " + (confused ? "#e74c3c" : TEXT_DIM) + "; -fx-font-size: 12px;");
        tp .setText("Type:   " + type);
        en .setText("Energy: " + energy);
        ps .setText("Cell:   " + position);
        st .setText("Status: " + status);
    }

    /**
     * Loads the monster's portrait into the stat card.
     * Call this from GameController.initializeView() once the monsters are known:
     *
     *   gameView.setPlayerPhoto(true,  game.getPlayer()  .getName(), ...);
     *   gameView.setPlayerPhoto(false, game.getOpponent().getName(), ...);
     *
     * Image looked up by engine name → filename mapping (monsterImageKey).
     * Tries /resources/images/ and /images/ with both .png and .jpg automatically.
     */
    public void setPlayerPhoto(boolean isPlayer, String monsterName, String role) {
        StackPane pane   = isPlayer ? p1PhotoPane : p2PhotoPane;
        String    accent = isPlayer ? "#00bcd4"   : "#e91e63";

        String    key = monsterImageKey(monsterName);
        ImageView iv  = safeMonsterImage(key, 68, 68);

        pane.getChildren().clear();
        if (iv != null) {
            iv.setClip(new Circle(34, 34, 34));
            iv.setEffect(new DropShadow(14, Color.web(accent, 0.55)));
            pane.getChildren().add(iv);
        } else {
            System.out.println("[GameView] Monster image not found — name='"
                + monsterName + "'  key='" + key + "'");
            Label fb = new Label(isPlayer ? "P" : "O");
            fb.setFont(loadFont(F_BANGERS, 28));
            fb.setStyle("-fx-text-fill: " + accent + ";");
            pane.getChildren().add(fb);
        }
    }

    /** "YOUR TURN!" / "OPPONENT'S TURN!" — set by GameController. */
    public void updateLog(String message) { logLabel.setText(message); }

    /**
     * Dice glow:
     *   true  → cyan  (player's turn, clickable)
     *   false → red   (opponent's turn, dimmed)
     */
    public void setDiceTurnIndicator(boolean isPlayerTurn) {
        DropShadow glow = new DropShadow(26, isPlayerTurn ? Color.CYAN : Color.RED);
        glow.setSpread(0.40);
        diceWrapper.setEffect(glow);
        diceWrapper.setOpacity(isPlayerTurn ? 1.0 : 0.65);
    }

    /** Door energy value in GOLD at the cell's bottom-right corner. */
    public void setDoorEnergyLabel(int index, int energy) {
        if (index < 0 || index >= 100) return;
        Label enLbl = new Label(String.valueOf(energy));
        enLbl.setFont(loadFont(F_INTER, 11));
        enLbl.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold;");
        StackPane.setAlignment(enLbl, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(enLbl, new Insets(0, 3, 2, 0));
        cells[index].getChildren().add(enLbl);
    }

    /** Replaces the door cell background with the exhausted_door image. */
    public void markDoorExhausted(int index) {
        if (index < 0 || index >= 100) return;
        StackPane cell = cells[index];
        if (!cell.getChildren().isEmpty()
                && cell.getChildren().get(0) instanceof ImageView) {
            cell.getChildren().remove(0);
        }
        ImageView ex = safeImage("exhausted_door", 53, 53);
        if (ex != null) cell.getChildren().add(0, ex);
        else            cell.setStyle("-fx-background-color: #4a4a4a; -fx-opacity: 0.55;");
    }

    /** Shows the dice face and numeric result in the bottom label. */
    public void showDiceResult(int roll) {
        String[] faces = {"", "⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};
        String face = (roll >= 1 && roll <= 6) ? faces[roll] : "?";
        diceLabel.setText(face + "  Rolled: " + roll);
    }

    /** Warning popup — closing it does NOT stop the game. */
    public void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    // ── Getters for GameController ────────────────────────────────────
    public Button    getPowerBtn()      { return powerBtn;      }
    public ImageView getDiceImageView() { return diceImageView; }

    // ═══════════════════ private helpers ════════════════════════════

    /**
     * Maps the engine's monster name to its image filename (no extension).
     * Engine names from the spec:
     *   "James P. Sullivan" → James_Sullivan
     *   "Mike Wazowski"     → Mike_Wazowski
     *   "Randall Boggs"     → Randall_Boggs
     *   "Celia Mae"         → Celia_Mae
     *   "Henry J. Waternoose" → Henry_Waternoose
     *   "Roz"               → Roz
     *   "Fungus"            → Fungus
     *   "Yeti"              → Yeti
     */
    private String monsterImageKey(String name) {
        if (name.contains("Sullivan"))   return "James_Sullivan";
        if (name.contains("Wazowski"))   return "Mike_Wazowski";
        if (name.contains("Randall"))    return "Randall_Boggs";
        if (name.contains("Celia"))      return "Celia_Mae";
        if (name.contains("Waternoose")) return "Henry_Waternoose";
        if (name.contains("Roz"))        return "Roz";
        if (name.contains("Fungus"))     return "Fungus";
        if (name.contains("Yeti"))       return "Yeti";
        return name.trim().replace(" ", "_").replace(".", "").replace("'", "");
    }

    /**
     * Tries four path variants in order so minor project-structure
     * differences don't prevent the image from loading:
     *   1. /resources/images/<key>.png
     *   2. /images/<key>.png
     *   3. /resources/images/<key>.jpg
     *   4. /images/<key>.jpg
     */
    private ImageView safeMonsterImage(String key, double w, double h) {
        String[] paths = {
            "/resources/images/" + key + ".png",
            "/images/"           + key + ".png",
            "/resources/images/" + key + ".jpg",
            "/images/"           + key + ".jpg",
        };
        for (String path : paths) {
            try {
                InputStream s = getClass().getResourceAsStream(path);
                if (s != null) {
                    ImageView iv = new ImageView(new Image(s, w, h, false, true));
                    iv.setFitWidth(w);
                    iv.setFitHeight(h);
                    iv.setPreserveRatio(false);
                    return iv;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    /** Cell / UI images (board tiles, dice, etc.) from /resources/images/. */
    private ImageView safeImage(String name, double w, double h) {
        try {
            InputStream s = getClass().getResourceAsStream("/resources/images/" + name + ".png");
            if (s == null) return null;
            ImageView iv = new ImageView(new Image(s, w, h, false, true));
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            iv.setPreserveRatio(false);
            return iv;
        } catch (Exception e) {
            return null;
        }
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

    private Font loadFont(String resourcePath, double size) {
        try {
            InputStream stream = getClass().getResourceAsStream("/" + resourcePath);
            if (stream != null) {
                Font f = Font.loadFont(stream, size);
                if (f != null) return f;
            }
        } catch (Exception ignored) {}
        return Font.font("System", FontWeight.BOLD, size);
    }

    private boolean has(int[] arr, int val) {
        for (int v : arr) if (v == val) return true;
        return false;
    }
}