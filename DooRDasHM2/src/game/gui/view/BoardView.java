package game.gui.view;

import game.engine.Constants;
import game.gui.ResourceLoader;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * 10×10 game board.
 * Each cell = base normal_tile + rounded-rect cell-type overlay with glow accent.
 * Player tokens are circular photos that slide along the zigzag path.
 */
public class BoardView extends StackPane {

    private static final int    CELL_SIZE   = 54;
    private static final int    OVERLAY_SZ  = 40;  // overlay image size inside cell
    private static final int    TOKEN_SIZE  = 32;
    private static final int    STEP_MS     = 120;  // ms per cell in animation

    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String GOLD      = "#f1c40f";

    private final GridPane    gameBoard   = new GridPane();
    private final Pane        overlayPane = new Pane();        // tokens live here
    private final StackPane[] cells       = new StackPane[100];

    // tokens
    private final StackPane playerToken   = new StackPane();
    private final StackPane opponentToken = new StackPane();
    private int   playerPos   = 0;
    private int   opponentPos = 0;
    private String playerImgKey   = null;
    private String opponentImgKey = null;

    // exhausted door tracker
    private final boolean[] doorExhausted = new boolean[100];

    public BoardView() {
        buildGrid();
        placeTokens();
        this.getChildren().addAll(gameBoard, overlayPane);
        overlayPane.setMouseTransparent(true);
        overlayPane.prefWidthProperty().bind(this.widthProperty());
        overlayPane.prefHeightProperty().bind(this.heightProperty());
    }

    // ── public API ────────────────────────────────────────────────────────────

    /** Set the monster photo key used for this player's board token. */
    public void setTokenImage(boolean isPlayer, String monsterName) {
        if (isPlayer) playerImgKey = monsterName;
        else           opponentImgKey = monsterName;
        rebuildToken(isPlayer);
    }

    /**
     * Animate the token along the zigzag path to newPos.
     * onComplete is called once the last step finishes.
     */
    public void movePlayer(int newPos, boolean isPlayer, Runnable onComplete) {
        int from = isPlayer ? playerPos : opponentPos;
        if (isPlayer) playerPos = newPos; else opponentPos = newPos;

        StackPane token = isPlayer ? playerToken : opponentToken;

        List<Integer> path = buildPath(from, newPos);
        if (path.isEmpty()) { if (onComplete != null) onComplete.run(); return; }

        animateAlongPath(token, path, 0, onComplete);
    }

    /** Backward compat (no callback). */
    public void movePlayer(int newPos, boolean isPlayer) {
        movePlayer(newPos, isPlayer, null);
    }

    public void setDoorEnergyLabel(int index, int energy) {
        if (outOfRange(index)) return;
        Label lbl = new Label(String.valueOf(energy));
        lbl.setFont(font(F_INTER, 10));
        lbl.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold;");
        StackPane.setAlignment(lbl, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(lbl, new Insets(0, 2, 2, 0));
        cells[index].getChildren().add(lbl);
    }

    public void addMonsterNameLabel(int idx, String name) {
        if (outOfRange(idx)) return;
        Label lbl = new Label(name);
        lbl.setFont(font(F_INTER, 7));
        lbl.setStyle(
            "-fx-text-fill: white;" +
            "-fx-background-color: rgba(0,0,0,0.70);" +
            "-fx-background-radius: 3;" +
            "-fx-padding: 1 2;"
        );
        lbl.setWrapText(false);
        lbl.setAlignment(Pos.CENTER);
        StackPane.setAlignment(lbl, Pos.BOTTOM_CENTER);
        StackPane.setMargin(lbl, new Insets(0, 0, 2, 0));
        cells[idx].getChildren().add(lbl);
    }

    public void markDoorActivated(int index) {
        if (outOfRange(index) || doorExhausted[index]) return;
        doorExhausted[index] = true;
        StackPane cell = cells[index];

        // remove existing overlay pane (second child after base tile)
        cell.getChildren().removeIf(n -> "overlay".equals(n.getId()));

        // grey / dark-purple translucent overlay
        Rectangle exhaustOverlay = new Rectangle(CELL_SIZE, CELL_SIZE);
        exhaustOverlay.setFill(Color.web("#2d1b4e", 0.72));
        exhaustOverlay.setArcWidth(6); exhaustOverlay.setArcHeight(6);
        exhaustOverlay.setId("overlay");
        cell.getChildren().add(exhaustOverlay);

        // small "used" label
        Label used = new Label("USED");
        used.setFont(font(F_PIXEL, 5));
        used.setStyle("-fx-text-fill: rgba(255,255,255,0.55);");
        used.setId("overlay");
        StackPane.setAlignment(used, Pos.CENTER);
        cell.getChildren().add(used);
    }

    // ── grid construction ─────────────────────────────────────────────────────

    private void buildGrid() {
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setHgap(2); gameBoard.setVgap(2);
        gameBoard.setPadding(new Insets(6));
        gameBoard.setStyle(
            "-fx-background-color: rgba(108,52,131,0.13);" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(155,89,182,0.40);" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1.5;"
        );

        for (int i = 0; i < 100; i++) {
            cells[i] = buildCell(i);
            int row = 9 - (i / 10);
            int col = (i / 10) % 2 == 0 ? (i % 10) : (9 - (i % 10));
            gameBoard.add(cells[i], col, row);
        }
    }

    private StackPane buildCell(int i) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);

        // 1. base tile
        ImageView base = iv("normal_tile", CELL_SIZE, CELL_SIZE);
        if (base != null) cell.getChildren().add(base);
        else              cell.setStyle("-fx-background-color: #1a1a2e;");

        // 2. cell-type overlay (rounded rect + glow)
        String overlayKey = overlayImageKey(i);
        if (overlayKey != null) {
            StackPane overlayPane = buildOverlay(overlayKey, i);
            cell.getChildren().add(overlayPane);
        }

        // 3. start / end badge
        if (i == 0 || i == 99) addBadge(cell, i == 0 ? "START" : "END", i == 0 ? "#16a085" : "#f39c12");

        // 4. cell number
        Label num = new Label(String.valueOf(i));
        num.setFont(font(F_INTER, 9));
        num.setStyle("-fx-text-fill: rgba(255,255,255,0.75); -fx-font-weight: bold;");
        StackPane.setAlignment(num, Pos.TOP_LEFT);
        StackPane.setMargin(num, new Insets(2, 0, 0, 2));
        cell.getChildren().add(num);

        return cell;
    }

    private StackPane buildOverlay(String imgKey, int cellIndex) {
        boolean isDoor     = (cellIndex % 2 == 1);
        String  glowColor  = glowColor(cellIndex);
        double  borderW    = isDoor ? 2.0 : 1.5;
        double  glowRadius = isDoor ? 12   : 7;
        double  glowAlpha  = isDoor ? 0.85 : 0.55;

        StackPane pane = new StackPane();
        pane.setMaxSize(OVERLAY_SZ + 4, OVERLAY_SZ + 4);

        ImageView img = iv(imgKey, OVERLAY_SZ, OVERLAY_SZ);
        if (img != null) {
            Rectangle clip = new Rectangle(OVERLAY_SZ, OVERLAY_SZ);
            clip.setArcWidth(10); clip.setArcHeight(10);
            img.setClip(clip);
            pane.getChildren().add(img);
        }

        // Rounded border with glow
        Rectangle border = new Rectangle(OVERLAY_SZ, OVERLAY_SZ);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.web(glowColor, isDoor ? 0.90 : 0.65));
        border.setStrokeWidth(borderW);
        border.setArcWidth(10); border.setArcHeight(10);
        DropShadow ds = new DropShadow(glowRadius, Color.web(glowColor, glowAlpha));
        border.setEffect(ds);
        pane.getChildren().add(border);

        return pane;
    }

    private void addBadge(StackPane cell, String text, String bg) {
        Label l = new Label(text);
        l.setFont(font(F_PIXEL, 5));
        l.setStyle("-fx-text-fill: white; -fx-background-color: " + bg + "; -fx-background-radius: 3; -fx-padding: 1 3;");
        StackPane.setAlignment(l, Pos.CENTER);
        cell.getChildren().add(l);
    }

    // ── token setup ───────────────────────────────────────────────────────────

    private void placeTokens() {
        rebuildToken(true);
        rebuildToken(false);

        // position both at cell 0
        overlayPane.sceneProperty().addListener((obs, o, scene) -> {
            if (scene != null) {
                scene.getRoot().applyCss();
                scene.getRoot().layout();
                positionToken(playerToken,   0);
                positionToken(opponentToken, 0);
            }
        });
    }

    private void rebuildToken(boolean isPlayer) {
        StackPane token = isPlayer ? playerToken : opponentToken;
        String    key   = isPlayer ? playerImgKey : opponentImgKey;
        String    color = isPlayer ? "#00bcd4" : "#e91e63";
        token.getChildren().clear();
        token.setPrefSize(TOKEN_SIZE, TOKEN_SIZE);
        token.setMaxSize(TOKEN_SIZE, TOKEN_SIZE);

        Image img = (key != null) ? monsterImage(key, TOKEN_SIZE, TOKEN_SIZE) : null;
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(TOKEN_SIZE); iv.setFitHeight(TOKEN_SIZE); iv.setPreserveRatio(false);
            iv.setClip(new Circle(TOKEN_SIZE / 2.0, TOKEN_SIZE / 2.0, TOKEN_SIZE / 2.0));
            token.getChildren().add(iv);
        } else {
            Circle c = new Circle(TOKEN_SIZE / 2.0);
            c.setFill(Color.web(color)); c.setStroke(Color.WHITE); c.setStrokeWidth(2);
            Label l = new Label(isPlayer ? "P" : "O");
            l.setFont(font(F_INTER, 12));
            l.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            token.getChildren().addAll(c, l);
        }

        Circle borderCircle = new Circle(TOKEN_SIZE / 2.0);
        borderCircle.setFill(Color.TRANSPARENT);
        borderCircle.setStroke(Color.web(color));
        borderCircle.setStrokeWidth(2.5);
        borderCircle.setEffect(new DropShadow(12, Color.web(color, 0.80)));
        token.getChildren().add(borderCircle);

        if (!overlayPane.getChildren().contains(token))
            overlayPane.getChildren().add(token);
    }

    // ── animation ─────────────────────────────────────────────────────────────

    private void animateAlongPath(StackPane token, List<Integer> path, int step, Runnable onComplete) {
        if (step >= path.size()) {
            if (onComplete != null) onComplete.run();
            return;
        }
        Point2D target = cellOverlayCenter(path.get(step));
        if (target == null) {
            animateAlongPath(token, path, step + 1, onComplete);
            return;
        }
        Timeline tl = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(token.layoutXProperty(), token.getLayoutX()),
                new KeyValue(token.layoutYProperty(), token.getLayoutY())
            ),
            new KeyFrame(Duration.millis(STEP_MS),
                new KeyValue(token.layoutXProperty(), target.getX(), Interpolator.EASE_BOTH),
                new KeyValue(token.layoutYProperty(), target.getY(), Interpolator.EASE_BOTH)
            )
        );
        tl.setOnFinished(e -> animateAlongPath(token, path, step + 1, onComplete));
        tl.play();
    }

    private void positionToken(StackPane token, int cellIndex) {
        Point2D p = cellOverlayCenter(cellIndex);
        if (p != null) { token.setLayoutX(p.getX()); token.setLayoutY(p.getY()); }
    }

    private Point2D cellOverlayCenter(int cellIndex) {
        if (outOfRange(cellIndex)) return null;
        StackPane cell = cells[cellIndex];
        try {
            Bounds gridBounds = gameBoard.localToParent(gameBoard.getBoundsInLocal());
            Bounds cellBounds = cell.getBoundsInParent();      // relative to GridPane
            double cx = gridBounds.getMinX() + gameBoard.getPadding().getLeft()  + cellBounds.getMinX() + cellBounds.getWidth()  / 2 - TOKEN_SIZE / 2.0;
            double cy = gridBounds.getMinY() + gameBoard.getPadding().getTop()   + cellBounds.getMinY() + cellBounds.getHeight() / 2 - TOKEN_SIZE / 2.0;
            return new Point2D(cx, cy);
        } catch (Exception e) { return null; }
    }

    private List<Integer> buildPath(int from, int to) {
        List<Integer> path = new ArrayList<>();
        int step = to > from ? 1 : -1;
        for (int i = from + step; i != to + step; i += step) path.add(i);
        return path;
    }

    // ── image / glow helpers ──────────────────────────────────────────────────

    private String overlayImageKey(int i) {
        if (has(Constants.CONVEYOR_CELL_INDICES, i)) return "Conveyor_Belts";
        if (has(Constants.SOCK_CELL_INDICES,     i)) return "Contamination_Socks";
        if (has(Constants.CARD_CELL_INDICES,     i)) return "Card_Cell";
        if (has(Constants.MONSTER_CELL_INDICES,  i)) return monsterImgForCellIndex(i);
        if (i % 2 == 1) return (i / 2) % 2 == 0 ? "red_door" : "purple_door";
        return null; // normal cell
    }

    private String glowColor(int i) {
        if (i % 2 == 1) return (i / 2) % 2 == 0 ? "#e91e63" : "#9c27b0"; // magenta / purple
        if (has(Constants.MONSTER_CELL_INDICES,  i)) return "#00bcd4"; // cyan
        if (has(Constants.CARD_CELL_INDICES,     i)) return "#009688"; // teal
        if (has(Constants.SOCK_CELL_INDICES,     i)) return "#ff5722"; // orange
        if (has(Constants.CONVEYOR_CELL_INDICES, i)) return "#7c4dff"; // violet
        return "transparent";
    }

    /** Cycle through the 6 remaining monster images for the 6 monster cells. */
    private String monsterImgForCellIndex(int cellIndex) {
        String[] imgs = { "James_Sullivan","Mike_Wazowski","Randall_Boggs","Celia_Mae","Roz","Fungus" };
        int[] monsterCells = Constants.MONSTER_CELL_INDICES;
        for (int k = 0; k < monsterCells.length; k++)
            if (monsterCells[k] == cellIndex) return imgs[k % imgs.length];
        return "James_Sullivan";
    }

    private Image monsterImage(String name, double w, double h) {
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

    private ImageView iv(String key, double w, double h) {
        Image img = ResourceLoader.loadImage(key, w, h);
        if (img == null) return null;
        ImageView iv = new ImageView(img); iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(false);
        return iv;
    }

    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
    private boolean has(int[] arr, int v) { for (int x : arr) if (x == v) return true; return false; }
    private boolean outOfRange(int i)     { return i < 0 || i >= 100; }
}
