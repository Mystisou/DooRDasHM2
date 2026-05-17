package game.gui.view;

import game.engine.Constants;
import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class InstructionsView extends BorderPane {

    private static final String BG_DARK      = "#0d0d1a";
    private static final String BG_MID       = "#1a1a2e";
    private static final String GOLD         = "#f1c40f";
    private static final String PURPLE_LIGHT = "#9b59b6";
    private static final String TEXT_DIM     = "#95a5a6";
    private static final String RED_BTN      = "#c0392b";
    private static final String RED_HOVER    = "#e74c3c";

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    private Button startBtn;

    public InstructionsView(String role) {
        this.setStyle("-fx-background-color: " + BG_DARK + ";");
        this.setPadding(new Insets(8, 14, 0, 14));
        buildUI();
    }

    
    public Button getStartButton() { return startBtn; }

    

    private void buildUI() {
        Label title = new Label("DooR DasH");
        title.setFont(font(F_BANGERS, 48));
        title.setStyle(
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-effect: dropshadow(gaussian,#6c3483,14,0.55,0,0);"
        );

        Label sub = new Label("Study the Floor  ·  Know your cells  ·  Then enter");
        sub.setFont(font(F_PIXEL, 8));
        sub.setStyle("-fx-text-fill: " + PURPLE_LIGHT + ";");

        VBox header = new VBox(3, title, sub);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(4, 0, 8, 0));

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

        startBtn = new Button("ENTER  THE  FLOOR");
        startBtn.setFont(font(F_PIXEL, 11));
        startBtn.setPrefSize(290, 56);
        applyBtnStyle(startBtn, RED_BTN);
        startBtn.setOnMouseEntered(e -> applyBtnStyle(startBtn, RED_HOVER));
        startBtn.setOnMouseExited(e  -> applyBtnStyle(startBtn, RED_BTN));
        

        HBox bottom = new HBox(startBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10, 0, 8, 0));
        bottom.setStyle("-fx-background-color: " + BG_DARK + ";");

        VBox root = new VBox(header, mainRow, bottom);
        VBox.setVgrow(mainRow, Priority.ALWAYS);
        this.setCenter(root);
    }

    private VBox buildBoardSection() {
        Label lbl = new Label("GAME BOARD");
        lbl.setFont(font(F_PIXEL, 10));
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

        
        ImageView base = imageView("normal_tile", 49, 49);
        if (base != null) cell.getChildren().add(base);
        else cell.setStyle("-fx-background-color: " + fallbackColour(i) + ";");

        
        String overlayKey = overlayImageKey(i);
        if (overlayKey != null) {
            ImageView ov = imageView(overlayKey, 36, 36);
            if (ov != null) {
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(36, 36);
                clip.setArcWidth(8); clip.setArcHeight(8);
                ov.setClip(clip);
                cell.getChildren().add(ov);
            }
        }

        
        Label num = new Label(String.valueOf(i));
        num.setFont(font(F_INTER, 9));
        num.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-weight: bold;");
        StackPane.setAlignment(num, Pos.TOP_LEFT);
        StackPane.setMargin(num, new Insets(2, 0, 0, 2));
        cell.getChildren().add(num);

        if (i == 0)  addBadge(cell, "START", "#16a085");
        if (i == 99) addBadge(cell, "END",   "#f39c12");
        return cell;
    }

    
    private String overlayImageKey(int i) {
        if (has(Constants.CONVEYOR_CELL_INDICES, i)) return "Conveyor_Belts";
        if (has(Constants.SOCK_CELL_INDICES,     i)) return "Contamination_Socks";
        if (has(Constants.CARD_CELL_INDICES,     i)) return "Card_Cell";
        if (has(Constants.MONSTER_CELL_INDICES,  i)) return monsterImgForCellIndex(i);
        if (i % 2 == 1) return (i / 2) % 2 == 0 ? "red_door" : "purple_door";
        return null;
    }

    private String monsterImgForCellIndex(int cellIndex) {
        String[] imgs = { "James_Sullivan","Mike_Wazowski","Randall_Boggs","Celia_Mae","Roz","Fungus" };
        int[] monsterCells = Constants.MONSTER_CELL_INDICES;
        for (int k = 0; k < monsterCells.length; k++)
            if (monsterCells[k] == cellIndex) return imgs[k % imgs.length];
        return "James_Sullivan";
    }

    private void addBadge(StackPane cell, String text, String bg) {
        Label l = new Label(text);
        l.setFont(font(F_PIXEL, 6));
        l.setStyle("-fx-text-fill: white; -fx-background-color: " + bg
            + "; -fx-background-radius: 3; -fx-padding: 1 3;");
        StackPane.setAlignment(l, Pos.CENTER);
        cell.getChildren().add(l);
    }

    private VBox buildDescSection() {
        Label hdr = new Label("CELL  TYPES");
        hdr.setFont(font(F_PIXEL, 10));
        hdr.setStyle("-fx-text-fill: " + GOLD + ";");
        hdr.setPadding(new Insets(0, 0, 10, 0));
        hdr.setAlignment(Pos.CENTER);
        hdr.setMaxWidth(Double.MAX_VALUE);
        hdr.setTextAlignment(TextAlignment.CENTER);

        String[][] entries = {
            {"red_door",            "Scarer Door",    "Scarers gain energy;\nLaughers lose it."},
            {"purple_door",         "Laugher Door",   "Laughers gain energy;\nScarers lose it."},
            {"James_Sullivan",      "Monster Cell",   "Same role: free powerup.\nOpposite: energy swap."},
            {"Conveyor_Belts",      "Conveyor Belt",  "Jump forward\nby belt amount."},
            {"Contamination_Socks", "Sock",           "Move back + lose\n100 energy."},
            {"Card_Cell",           "Card Cell",      "Draw a mystery\ncard!"},
        };

        VBox list = new VBox(8);
        for (String[] e : entries) list.getChildren().add(buildDescRow(e[0], e[1], e[2]));

        VBox panel = new VBox(10, hdr, list);
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

    private HBox buildDescRow(String imgName, String name, String desc) {
        StackPane imgBox = new StackPane();
        imgBox.setMinSize(40, 40); imgBox.setPrefSize(40, 40); imgBox.setMaxSize(40, 40);
        imgBox.setStyle("-fx-background-color: " + BG_MID + "; -fx-background-radius: 8;");
        ImageView iv = imageView(imgName, 34, 34);
        if (iv != null) imgBox.getChildren().add(iv);
        else { Circle c = new Circle(17); c.setFill(Color.web("#3d5166")); imgBox.getChildren().add(c); }

        Label nameLbl = new Label(name);
        nameLbl.setFont(font(F_BANGERS, 17));
        nameLbl.setStyle("-fx-text-fill: white;");
        nameLbl.setWrapText(true);

        Label descLbl = new Label(desc);
        descLbl.setFont(font(F_INTER, 11));
        descLbl.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        descLbl.setWrapText(true);

        VBox textBox = new VBox(2, nameLbl, descLbl);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox row = new HBox(8, imgBox, textBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 8, 5, 8));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 8;");
        return row;
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

    private ImageView imageView(String name, double w, double h) {
        Image img = ResourceLoader.loadImage(name, w, h);
        if (img == null) return null;
        ImageView iv = new ImageView(img); iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(false);
        return iv;
    }

    private String fallbackColour(int i) {
        if (i == 0)  return "#16a085"; if (i == 99) return "#f39c12";
        if (has(Constants.CONVEYOR_CELL_INDICES, i)) return "#27ae60";
        if (has(Constants.SOCK_CELL_INDICES,     i)) return "#d35400";
        if (has(Constants.CARD_CELL_INDICES,     i)) return "#c0392b";
        if (has(Constants.MONSTER_CELL_INDICES,  i)) return "#8e44ad";
        if (i % 2 == 1) return (i / 2) % 2 == 0 ? "#2980b9" : "#1abc9c";
        return (i / 10 + i % 10) % 2 == 0 ? "#2c3e50" : "#34495e";
    }

    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
    private boolean has(int[] arr, int val) { for (int v : arr) if (v == val) return true; return false; }
}
