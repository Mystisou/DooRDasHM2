package game.gui.view;

import game.engine.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * VIEW — Builds and updates the entire game screen.
 * No engine logic lives here.  The Controller calls the public
 * methods (movePlayer, updateMonsterCard, showDiceResult, etc.)
 * to reflect model changes on screen.
 *
 * Layout:
 * TOP    : centre log label + horizontal legend flow pane
 * SIDES  : player stat card (left) + opponent stat card (right)
 * CENTRE : game board (GridPane)
 * BOTTOM : Power button | dice-result label | Roll button
 */
public class GameView extends BorderPane {

    // ── Controls ──────────────────────────────────────────────────────
    private Button rollBtn;
    private Button powerBtn;
    private Label  diceLabel;   // shows rolled value after each turn

    // ── Board ─────────────────────────────────────────────────────────
    private GridPane    gameBoard;
    private StackPane[] cells = new StackPane[100]; // index == game cell #

    // ── Player tokens (placed inside cells) ───────────────────────────
    private StackPane playerToken;    // cyan  – the human player
    private StackPane opponentToken;  // magenta – the AI opponent

    // ── Top bar widgets ────────────────────────────────────────────────
    private Label logLabel;           // current-turn message, centre-top

    // Stat-card labels (updated each turn via updateMonsterCard)
    private Label p1Name, p1OrigRole, p1CurRole, p1Type, p1Energy, p1Pos, p1Status;
    private Label p2Name, p2OrigRole, p2CurRole, p2Type, p2Energy, p2Pos, p2Status;

    // ─────────────────────────────────────────────────────────────────
    public GameView() {
        this.setStyle("-fx-background-color: #1a252f;");
        this.setPadding(new Insets(10));

        buildTop();
        buildSides();
        buildCentreBoard();
        buildBottom();
    }

    // ═══════════════════════════ TOP BAR & SIDES ═════════════════════════════

    private void buildTop() {
        logLabel = new Label("⚔  Welcome to the Floor!");
        logLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 16px; -fx-font-weight: bold;");
        logLabel.setWrapText(true);
        logLabel.setMaxWidth(340);
        logLabel.setAlignment(Pos.CENTER);

        // Put the horizontal legend right below the log label
        FlowPane legend = buildLegendFlow();

        VBox top = new VBox(10, logLabel, legend);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(5, 0, 15, 0)); // small space from ceiling, more space for board

        this.setTop(top);
    }

    private void buildSides() {
        VBox p1Card = buildStatCard(true);
        VBox p2Card = buildStatCard(false);

        // Margins keep cards away from edge of screen and the board
        BorderPane.setMargin(p1Card, new Insets(0, 15, 0, 0)); // Right margin away from board
        BorderPane.setMargin(p2Card, new Insets(0, 0, 0, 15)); // Left margin away from board

        this.setLeft(p1Card);
        this.setRight(p2Card);
    }

    /** Builds one monster-stat card (left = player, right = opponent). */
    private VBox buildStatCard(boolean isPlayer) {
        // Avatar circle
        Circle avatar = new Circle(16);
        avatar.setFill(isPlayer ? Color.CYAN : Color.MAGENTA);
        avatar.setStroke(Color.WHITE);
        avatar.setStrokeWidth(2);
        Label avatarLetter = new Label(isPlayer ? "P" : "O");
        avatarLetter.setStyle("-fx-text-fill: #1a252f; -fx-font-weight: bold; -fx-font-size: 14px;");
        StackPane avatarPane = new StackPane(avatar, avatarLetter);

        Label header = new Label(isPlayer ? "  YOU" : "  OPPONENT");
        header.setStyle("-fx-text-fill: " + (isPlayer ? "#00bcd4" : "#e91e63")
                + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        HBox headerRow = new HBox(8, avatarPane, header);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        // Individual stat labels (fields so the controller can update them)
        Label nm   = styledStatLabel("#ecf0f1", 13, true);
        Label or_  = styledStatLabel("#bdc3c7", 12, false);
        Label cr   = styledStatLabel("#bdc3c7", 12, false);
        Label tp   = styledStatLabel("#bdc3c7", 12, false);
        Label en   = styledStatLabel("#2ecc71", 13, true);
        Label ps   = styledStatLabel("#3498db", 12, false);
        Label st   = styledStatLabel("#e67e22", 12, false);

        // Default placeholder text
        nm.setText("🦠 —"); or_.setText("Role:   —"); cr.setText("Active: —");
        tp.setText("Type:   —"); en.setText("⚡ Energy: —");
        ps.setText("📍 Cell: 0"); st.setText("✅ Normal");

        // Store references for updates
        if (isPlayer) { p1Name=nm; p1OrigRole=or_; p1CurRole=cr; p1Type=tp; p1Energy=en; p1Pos=ps; p1Status=st; }
        else          { p2Name=nm; p2OrigRole=or_; p2CurRole=cr; p2Type=tp; p2Energy=en; p2Pos=ps; p2Status=st; }

        Separator sep = new Separator();

        VBox card = new VBox(4, headerRow, sep, nm, or_, cr, tp, en, ps, st);
        // Added top padding to pull cards down from the ceiling on the left/right sides
        card.setPadding(new Insets(20, 15, 8, 15)); 
        card.setPrefWidth(210);
        card.setStyle(
            "-fx-background-color: #1e3044;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + (isPlayer ? "#00bcd4" : "#e91e63") + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1.5;"
        );
        return card;
    }

    private Label styledStatLabel(String colour, int size, boolean bold) {
        Label l = new Label();
        l.setStyle("-fx-text-fill: " + colour + "; -fx-font-size: " + size + "px;"
                + (bold ? " -fx-font-weight: bold;" : ""));
        return l;
    }

    // ═══════════════════════════ BOARD ═══════════════════════════════

    private void buildCentreBoard() {
        gameBoard = new GridPane();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setHgap(1);
        gameBoard.setVgap(1);
        gameBoard.setPadding(new Insets(4));
        gameBoard.setStyle("-fx-background-color: #0d1b2a; -fx-background-radius: 8;");

        // Build player tokens
        playerToken   = makeToken("P", Color.CYAN);
        opponentToken = makeToken("O", Color.MAGENTA);

        for (int i = 0; i < 100; i++) {
            StackPane cell = buildCell(i);
            cells[i] = cell;

            // Zigzag grid mapping: cell 0 = bottom-left, 99 = top-right
            int row = 9 - (i / 10);
            int col = (i / 10) % 2 == 0 ? (i % 10) : (9 - (i % 10));
            gameBoard.add(cell, col, row);
        }

        // Just assign the board directly to the center
        this.setCenter(gameBoard);
    }

    /** Creates and styles a single board cell based on its game-index type. */
    private StackPane buildCell(int i) {
        StackPane cell = new StackPane();
        // Returned back to full 53x53 since layout now permits it!
        cell.setPrefSize(53, 53);
        cell.setMinSize(53, 53);
        cell.setMaxSize(53, 53);

        cell.setStyle("-fx-background-color: " + getCellColour(i)
                + "; -fx-border-color: rgba(0,0,0,0.35); -fx-border-width: 0.5;");

        // Cell number — top-left corner, small
        Label numLbl = new Label(String.valueOf(i));
        numLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: rgba(255,255,255,0.55); -fx-font-weight: bold;");
        StackPane.setAlignment(numLbl, Pos.TOP_LEFT);
        StackPane.setMargin(numLbl, new Insets(2, 0, 0, 3));

        // Type icon — centre
        Label icon = new Label(getCellIcon(i));
        icon.setStyle("-fx-font-size: 17px;");

        cell.getChildren().addAll(numLbl, icon);
        return cell;
    }

    /** Coloured circle token with a letter inside. */
    private StackPane makeToken(String letter, Color colour) {
        Circle c = new Circle(12);
        c.setFill(colour);
        c.setStroke(Color.WHITE);
        c.setStrokeWidth(2);

        Label l = new Label(letter);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #1a252f;");

        StackPane token = new StackPane(c, l);
        token.setMaxSize(28, 28);
        return token;
    }

    /** Horizontal legend panel at the top of the board. */
    private FlowPane buildLegendFlow() {
        FlowPane box = new FlowPane();
        box.setHgap(15); 
        box.setVgap(8);
        box.setPadding(new Insets(8, 15, 8, 15));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #1e3044; -fx-background-radius: 10;");

        Label title = new Label("LEGEND:");
        title.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold; -fx-font-size: 13px;");

        box.getChildren().addAll(title,
            legendRow("🚪 Scarer Door",  "#2980b9"),
            legendRow("🚪 Laugher Door", "#1abc9c"),
            legendRow("🃏 Card Cell",    "#c0392b"),
            legendRow("🚌 Conveyor",     "#27ae60"),
            legendRow("🧦 Sock",         "#d35400"),
            legendRow("👾 Monster",      "#8e44ad"),
            legendRow("⬛ Normal",       "#2c3e50"),
            legendRow("● YOU",           "#00bcd4"),
            legendRow("● OPPONENT",      "#e91e63")
        );
        return box;
    }

    private HBox legendRow(String text, String hexColour) {
        Rectangle r = new Rectangle(12, 12);
        r.setFill(Color.web(hexColour));
        r.setArcWidth(3); r.setArcHeight(3);
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 11px;");
        HBox row = new HBox(6, r, l);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ═══════════════════════════ BOTTOM ══════════════════════════════

    private void buildBottom() {
        powerBtn = new Button("⚡ ACTIVATE POWER");
        powerBtn.setPrefSize(180, 46);
        powerBtn.setStyle(btnStyle("#e67e22", "#d35400"));

        rollBtn = new Button("🎲 ROLL DICE");
        rollBtn.setPrefSize(180, 46);
        rollBtn.setStyle(btnStyle("#27ae60", "#1e8449"));

        diceLabel = new Label("🎲  Roll to start!");
        diceLabel.setMinWidth(220);
        diceLabel.setAlignment(Pos.CENTER);
        diceLabel.setStyle(
            "-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;" +
            "-fx-background-color: #2c4a63; -fx-padding: 8 18; -fx-background-radius: 10;"
        );

        HBox bottom = new HBox(28, powerBtn, diceLabel, rollBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(12, 0, 4, 0));
        bottom.setStyle("-fx-background-color: #0d1b2a;");

        this.setBottom(bottom);
    }

    private String btnStyle(String base, String hover) {
        return "-fx-background-color: " + base + ";" +
               "-fx-text-fill: white;" +
               "-fx-font-weight: bold;" +
               "-fx-font-size: 14px;" +
               "-fx-background-radius: 8;" +
               "-fx-cursor: hand;";
    }

    // ═══════════════════ PUBLIC API — called by Controller ════════════

    /**
     * Moves the given player's token to the target cell.
     * Removes it from wherever it currently lives first.
     * Uses the cells[] array directly — O(1), no GridPane scan needed.
     */
    public void movePlayer(int newPos, boolean isCurrentPlayer) {
        StackPane token = isCurrentPlayer ? playerToken : opponentToken;

        // Remove from old cell (safe — only if currently parented to a StackPane)
        if (token.getParent() instanceof StackPane) {
            ((StackPane) token.getParent()).getChildren().remove(token);
        }

        // Place in the new cell
        if (newPos >= 0 && newPos < 100) {
            StackPane target = cells[newPos];
            target.getChildren().add(token);
            StackPane.setAlignment(token, isCurrentPlayer ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        }
    }

    /**
     * Updates every field inside one monster's stat card.
     * @param isPlayer  true = left card (YOU), false = right card (OPPONENT)
     */
    public void updateMonsterCard(boolean isPlayer,
                                   String name, String origRole, String curRole,
                                   String type, int energy, int position, String status) {
        Label nm  = isPlayer ? p1Name    : p2Name;
        Label or_ = isPlayer ? p1OrigRole: p2OrigRole;
        Label cr  = isPlayer ? p1CurRole : p2CurRole;
        Label tp  = isPlayer ? p1Type    : p2Type;
        Label en  = isPlayer ? p1Energy  : p2Energy;
        Label ps  = isPlayer ? p1Pos     : p2Pos;
        Label st  = isPlayer ? p1Status  : p2Status;

        boolean confused = !origRole.equals(curRole);

        nm .setText("🦠 " + name);
        or_.setText("Role:   " + origRole);
        cr .setText("Active: " + curRole + (confused ? "  ⚠ CONFUSED" : ""));
        cr .setStyle("-fx-text-fill: " + (confused ? "#e74c3c" : "#bdc3c7") + "; -fx-font-size: 12px;");
        tp .setText("Type:   " + type);
        en .setText("⚡ Energy: " + energy);
        ps .setText("📍 Cell:   " + position);
        st .setText(status);
    }

    /** Sets an energy-value sub-label on a door cell (called once during init). */
    public void setDoorEnergyLabel(int index, int energy) {
        if (index < 0 || index >= 100) return;
        StackPane cell = cells[index];
        Label enLbl = new Label(String.valueOf(energy));
        enLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: rgba(255,255,255,0.8); -fx-font-weight: bold;");
        StackPane.setAlignment(enLbl, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(enLbl, new Insets(0, 3, 2, 0));
        cell.getChildren().add(enLbl);
    }

    /** Marks an exhausted door cell visually (greyed-out). */
    public void markDoorExhausted(int index) {
        if (index < 0 || index >= 100) return;
        cells[index].setStyle("-fx-background-color: #4a4a4a; -fx-border-color: rgba(0,0,0,0.35); -fx-border-width: 0.5; -fx-opacity: 0.6;");
    }

    /** Shows the dice result prominently in the bottom bar. */
    public void showDiceResult(int roll) {
        String[] faces = {"", "⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};
        String face = (roll >= 1 && roll <= 6) ? faces[roll] : "🎲";
        diceLabel.setText(face + "  Rolled: " + roll);
    }

    public void updateLog(String message) {
        logLabel.setText(message);
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Button getRollBtn()  { return rollBtn;  }
    public Button getPowerBtn() { return powerBtn; }

    // ═══════════════════════ CELL COLOUR / ICON ══════════════════════

    private String getCellColour(int i) {
        if (i == 0)  return "#16a085"; // start
        if (i == 99) return "#f39c12"; // goal
        if (has(Constants.CONVEYOR_CELL_INDICES, i)) return "#27ae60";
        if (has(Constants.SOCK_CELL_INDICES,     i)) return "#d35400";
        if (has(Constants.CARD_CELL_INDICES,     i)) return "#c0392b";
        if (has(Constants.MONSTER_CELL_INDICES,  i)) return "#8e44ad";
        if (i % 2 == 1) {
            // Door: odd-indexed; alternate SCARER (blue) / LAUGHER (teal)
            return (i / 2) % 2 == 0 ? "#2980b9" : "#1abc9c";
        }
        // Normal: dark checkerboard
        int row = i / 10, col = i % 10;
        return (row + col) % 2 == 0 ? "#2c3e50" : "#34495e";
    }

    private String getCellIcon(int i) {
        if (i == 0)  return "🏁";
        if (i == 99) return "⭐";
        if (has(Constants.CONVEYOR_CELL_INDICES, i)) return "🚌";
        if (has(Constants.SOCK_CELL_INDICES,     i)) return "🧦";
        if (has(Constants.CARD_CELL_INDICES,     i)) return "🃏";
        if (has(Constants.MONSTER_CELL_INDICES,  i)) return "👾";
        if (i % 2 == 1) return "🚪";
        return "";
    }

    private boolean has(int[] arr, int val) {
        for (int v : arr) if (v == val) return true;
        return false;
    }
}