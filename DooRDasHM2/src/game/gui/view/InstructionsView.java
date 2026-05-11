package game.gui.view;

import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.gui.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.InputStream;

/**
 * Pre-game instructions screen.
 *
 * Layout  (1000 × 800 window):
 *   TOP    : DooR DasH title + subtitle
 *   CENTRE : HBox — static board (left, natural size) | 2-col cell guide (right, fills rest)
 *   BOTTOM : ENTER THE FLOOR button (thick, red, PressStart2P)
 */
public class InstructionsView extends BorderPane {

    // ── Palette — mirrors StartView ────────────────────────────────────
    private static final String BG_DARK      = "#0d0d1a";
    private static final String BG_MID       = "#1a1a2e";
    private static final String GOLD         = "#f1c40f";
    private static final String PURPLE_LIGHT = "#9b59b6";
    private static final String TEXT_MAIN    = "#ecf0f1";
    private static final String TEXT_DIM     = "#95a5a6";
    private static final String RED_BTN      = "#c0392b";
    private static final String RED_HOVER    = "#e74c3c";

    // ── Font paths — mirrors StartView ────────────────────────────────
    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-Regular.ttf";

    private final String selectedRole;
    private Button startBtn;

    // ─────────────────────────────────────────────────────────────────
    public InstructionsView(String role) {
        this.selectedRole = role;
        this.setStyle("-fx-background-color: " + BG_DARK + ";");
        this.setPadding(new Insets(8, 14, 0, 14));
        buildUI();
    }

    // ═══════════════════════════ BUILD ═══════════════════════════════

    private void buildUI() {

        /* ── Header ── */
        Label title = new Label("DooR DasH");
        title.setFont(loadFont(F_BANGERS, 48));
        title.setStyle(
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-effect: dropshadow(gaussian,#6c3483,14,0.55,0,0);"
        );

        Label sub = new Label("Study the Floor  ·  Know your cells  ·  Then enter");
        sub.setFont(loadFont(F_PIXEL, 8));
        sub.setStyle("-fx-text-fill: " + PURPLE_LIGHT + ";");

        VBox header = new VBox(3, title, sub);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(4, 0, 8, 0));

        /* ── Main split: 60 % board | 40 % cell guide ── */
        GridPane mainRow = new GridPane();
        mainRow.setHgap(18);
        VBox.setVgrow(mainRow, Priority.ALWAYS);

        ColumnConstraints boardCol = new ColumnConstraints();
        boardCol.setPercentWidth(60);
        boardCol.setHalignment(javafx.geometry.HPos.CENTER);
        ColumnConstraints descCol  = new ColumnConstraints();
        descCol.setPercentWidth(40);
        mainRow.getColumnConstraints().addAll(boardCol, descCol);

        RowConstraints mainRowC = new RowConstraints();
        mainRowC.setVgrow(Priority.ALWAYS);
        mainRow.getRowConstraints().add(mainRowC);

        VBox boardSection = buildBoardSection();
        VBox descSection  = buildDescSection();
        GridPane.setFillHeight(boardSection, true);
        GridPane.setFillHeight(descSection,  true);
        GridPane.setVgrow(boardSection, Priority.ALWAYS);
        GridPane.setVgrow(descSection,  Priority.ALWAYS);

        mainRow.add(boardSection, 0, 0);
        mainRow.add(descSection,  1, 0);

        /* ── Bottom bar — button is taller (56 px) and sits higher ── */
        startBtn = new Button("ENTER  THE  FLOOR");
        startBtn.setFont(loadFont(F_PIXEL, 11));
        startBtn.setPrefSize(290, 56);          // taller than before (was 46)
        applyBtnStyle(startBtn, RED_BTN);
        startBtn.setOnMouseEntered(e -> applyBtnStyle(startBtn, RED_HOVER));
        startBtn.setOnMouseExited(e  -> applyBtnStyle(startBtn, RED_BTN));
        startBtn.setOnAction(e -> {
            try {
                Game     game     = new Game(Role.valueOf(selectedRole));
                GameView gameView = new GameView();
                new GameController(game, gameView);
                ViewManager.updateView(gameView);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox bottom = new HBox(startBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10, 0, 8, 0));   // less padding → button sits higher
        bottom.setStyle("-fx-background-color: " + BG_DARK + ";");

        VBox root = new VBox(header, mainRow, bottom);
        VBox.setVgrow(mainRow, Priority.ALWAYS);
        this.setCenter(root);
    }

    // ══════════════════ Board section ════════════════════════════════

    private VBox buildBoardSection() {
        Label lbl = new Label("GAME BOARD");
        lbl.setFont(loadFont(F_PIXEL, 10));
        lbl.setStyle("-fx-text-fill: " + GOLD + ";");
        lbl.setPadding(new Insets(0, 0, 6, 0));

        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setHgap(1);
        board.setVgap(1);
        board.setPadding(new Insets(6));
        board.setStyle(
            "-fx-background-color: rgba(108,52,131,0.13);" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(155,89,182,0.40);" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1.5;"
        );

        for (int i = 0; i < 100; i++) {
            int row = 9 - (i / 10);
            int col = (i / 10) % 2 == 0 ? (i % 10) : (9 - (i % 10));
            board.add(buildStaticCell(i), col, row);
        }

        VBox box = new VBox(6, lbl, board);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private StackPane buildStaticCell(int i) {
        StackPane cell = new StackPane();
        cell.setPrefSize(49, 49);
        cell.setMinSize(49, 49);
        cell.setMaxSize(49, 49);

        ImageView bg = safeImage(cellImageName(i), 49, 49);
        if (bg != null) {
            cell.getChildren().add(bg);
        } else {
            cell.setStyle("-fx-background-color: " + fallbackColour(i)
                + "; -fx-border-color: rgba(0,0,0,0.3); -fx-border-width: 0.5;");
        }

        Label num = new Label(String.valueOf(i));
        num.setFont(loadFont(F_INTER, 11));
        num.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        StackPane.setAlignment(num, Pos.TOP_LEFT);
        StackPane.setMargin(num, new Insets(2, 0, 0, 2));
        cell.getChildren().add(num);

        if (i == 0)  addBadge(cell, "START", "#16a085");
        if (i == 99) addBadge(cell, "END",   "#f39c12");

        return cell;
    }

    private void addBadge(StackPane cell, String text, String bg) {
        Label l = new Label(text);
        l.setFont(loadFont(F_PIXEL, 6));
        l.setStyle("-fx-text-fill: white; -fx-background-color: " + bg
            + "; -fx-background-radius: 3; -fx-padding: 1 3;");
        StackPane.setAlignment(l, Pos.CENTER);
        cell.getChildren().add(l);
    }

    // ══════════════════ Cell-type guide — 2 columns, no scroll ═══════

    private VBox buildDescSection() {
        Label hdr = new Label("CELL  TYPES  &  EFFECTS");
        hdr.setFont(loadFont(F_PIXEL, 10));
        hdr.setStyle("-fx-text-fill: " + GOLD + ";");
        hdr.setPadding(new Insets(0, 0, 8, 0));

        // 2-column GridPane — 10 items → 5 rows, no scrolling needed
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);

        // 50 / 50 column split
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        // {image-name, display-name, short description}
        String[][] entries = {
            {"scarer_door",    "Scarer Door",       "Scarers gain energy;\nLaughers lose it."},
            {"laugher_door",   "Laugher Door",      "Laughers gain energy;\nScarers lose it."},
            {"exhausted_door", "Exhausted Door",    "Already used —\nno energy effect."},
            {"monster_cell",   "Monster Cell",      "Same role: free powerup.\nOpposite: energy swap."},
            {"conveyor",       "Conveyor Belt",     "Jump forward\nby belt amount."},
            {"sock",           "Sock",              "Move back + lose\n100 energy."},
            {"energy",         "Card Cell",         "Draw a mystery\ncard!"},
            {"shield",         "Shield",            "Blocks next negative\nenergy effect."},
            {"confusion",      "Confusion",         "Roles swapped\nfor N turns."},
            {"freeze",         "Freeze",            "Skip your\nnext turn."},
        };

        for (int i = 0; i < entries.length; i++) {
            grid.add(buildDescRow(entries[i][0], entries[i][1], entries[i][2]),
                     i % 2,   // column
                     i / 2);  // row
        }

        // Outer panel — same purple-tint box as StartView's howToPlay
        VBox panel = new VBox(10, hdr, grid);
        panel.setPadding(new Insets(14));
        panel.setStyle(
            "-fx-background-color: rgba(108,52,131,0.13);" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(155,89,182,0.40);" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1.5;"
        );
        VBox.setVgrow(panel, Priority.ALWAYS);

        VBox outer = new VBox(panel);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return outer;
    }

    /**
     * Single cell-type card — mirrors buildTipCard() from StartView:
     * image badge + name (Bangers) + short desc (Inter), rgba tip-card bg.
     */
    private HBox buildDescRow(String imgName, String name, String desc) {
        // Image badge
        StackPane imgBox = new StackPane();
        imgBox.setMinSize(40, 40);
        imgBox.setPrefSize(40, 40);
        imgBox.setMaxSize(40, 40);
        imgBox.setStyle("-fx-background-color: " + BG_MID + "; -fx-background-radius: 8;");

        ImageView iv = safeImage(imgName, 34, 34);
        if (iv != null) {
            imgBox.getChildren().add(iv);
        } else {
            Circle c = new Circle(17);
            c.setFill(Color.web("#3d5166"));
            imgBox.getChildren().add(c);
        }

        // Name — Bangers, white
        Label nameLbl = new Label(name);
        nameLbl.setFont(loadFont(F_BANGERS, 17));
        nameLbl.setStyle("-fx-text-fill: white;");
        nameLbl.setWrapText(true);

        // Description — Inter, dim
        Label descLbl = new Label(desc);
        descLbl.setFont(loadFont(F_INTER, 12));
        descLbl.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        descLbl.setWrapText(true);

        VBox textBox = new VBox(2, nameLbl, descLbl);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        // Row card — rgba(255,255,255,0.04) same as StartView buildTipCard
        HBox row = new HBox(8, imgBox, textBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 8, 6, 8));
        row.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04);" +
            "-fx-background-radius: 8;"
        );
        return row;
    }

    // ═══════════════════ Utilities ═══════════════════════════════════

    private void applyBtnStyle(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + color + ",14,0.5,0,0);"
        );
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

    /** Fixed image path to match StartView: /resources/images/<name>.png */
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

    private boolean has(int[] arr, int val) {
        for (int v : arr) if (v == val) return true;
        return false;
    }
}