package game.gui.view;

import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class MonsterPanelView extends VBox {

    private static final String BG_DARK   = "#0d0d1a";
    private static final String TEXT_MAIN = "#ecf0f1";
    private static final String TEXT_DIM  = "#95a5a6";
    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";
    private static final int    BAR_W     = 186;
    private static final int    WIN_ENERGY= 1000;

    private final boolean isPlayer;
    private final String  accent;

    private Label     nm, origRole, curRole, type, energyLbl, pos, status, deltaLbl;
    private StackPane photoPane;
    private Rectangle energyBarFg;

    public MonsterPanelView(boolean isPlayer) {
        this.isPlayer = isPlayer;
        this.accent   = isPlayer ? "#00bcd4" : "#e91e63";
        build();
    }

    

    public void update(String name, String origRoleStr, String curRoleStr,
                       String typeStr, int energyVal, int position, String statusStr) {
        boolean confused = !origRoleStr.equals(curRoleStr);
        deltaLbl.setText("");
        nm      .setText(name);
        origRole.setText("Role:   " + origRoleStr);
        curRole .setText("Active: " + curRoleStr + (confused ? "  CONFUSED" : ""));
        curRole .setStyle("-fx-text-fill: " + (confused ? "#e74c3c" : TEXT_DIM) + "; -fx-font-size: 12px;");
        applyConfusionBorder(confused);
        type    .setText("Type:   " + typeStr);
        energyLbl.setText("Energy: " + energyVal + " / " + WIN_ENERGY);
        pos     .setText("Cell:   " + position);
        status  .setText("Status: " + statusStr);
        updateEnergyBar(energyVal);
    }

    public void setPhoto(String monsterName) {
        Image img = monsterImage(monsterName, 68, 68);
        photoPane.getChildren().clear();
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(68); iv.setFitHeight(68); iv.setPreserveRatio(false);
            iv.setClip(new Circle(34, 34, 34));
            iv.setEffect(new DropShadow(14, Color.web(accent, 0.55)));
            photoPane.getChildren().add(iv);
        } else {
            Label fb = new Label(isPlayer ? "P" : "O");
            fb.setFont(font(F_BANGERS, 28));
            fb.setStyle("-fx-text-fill: " + accent + ";");
            photoPane.getChildren().add(fb);
        }
    }

    public void showEnergyDelta(int delta) {
        if (delta > 0) { deltaLbl.setText("▲  +" + delta); deltaLbl.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); }
        else if (delta < 0) { deltaLbl.setText("▼  " + delta); deltaLbl.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); }
        else deltaLbl.setText("");
    }

    

    private void build() {
        photoPane = new StackPane();
        photoPane.setMinSize(72, 72); photoPane.setPrefSize(72, 72); photoPane.setMaxSize(72, 72);
        photoPane.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-background-radius: 36;" +
            "-fx-border-color: " + accent + "BB;" +
            "-fx-border-width: 2.5;" +
            "-fx-border-radius: 36;"
        );
        Label fb = new Label(isPlayer ? "P" : "O");
        fb.setFont(font(F_BANGERS, 28)); fb.setStyle("-fx-text-fill: " + accent + ";");
        photoPane.getChildren().add(fb);

        Label headerLbl = new Label(isPlayer ? "YOU" : "OPPONENT");
        headerLbl.setFont(font(F_BANGERS, 22));
        headerLbl.setStyle("-fx-text-fill: white;");

        VBox topRow = new VBox(5, photoPane, headerLbl);
        topRow.setAlignment(Pos.CENTER);
        topRow.setPadding(new Insets(0, 0, 4, 0));

        nm       = statLbl(TEXT_MAIN, 13, true);
        origRole = statLbl(TEXT_DIM,  12, false);
        curRole  = statLbl(TEXT_DIM,  12, false);
        type     = statLbl(TEXT_DIM,  12, false);
        energyLbl= statLbl("#2ecc71", 12, true);
        pos      = statLbl("#3498db", 12, false);
        status   = statLbl("#e67e22", 12, false);

        nm       .setText("—");
        origRole .setText("Role:   —");
        curRole  .setText("Active: —");
        type     .setText("Type:   —");
        energyLbl.setText("Energy: 0 / " + WIN_ENERGY);
        pos      .setText("Cell:   0");
        status   .setText("Status: Normal");

        deltaLbl = new Label("");
        deltaLbl.setFont(font(F_INTER, 12));
        deltaLbl.setAlignment(Pos.CENTER);
        deltaLbl.setMaxWidth(Double.MAX_VALUE);

        
        StackPane energyBar = buildEnergyBar();

        Region divider = new Region();
        divider.setPrefHeight(1); divider.setMaxWidth(Double.MAX_VALUE);
        divider.setStyle("-fx-background-color: rgba(155,89,182,0.35);");

        this.getChildren().addAll(
            topRow, divider,
            nm, origRole, curRole, type,
            energyLbl, energyBar, deltaLbl,
            pos, status
        );
        this.setSpacing(5);
        this.setPadding(new Insets(12, 14, 12, 14));
        this.setPrefWidth(218);
        this.setAlignment(Pos.CENTER);
        this.setStyle(
            "-fx-background-color: " + accent + "1C;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + accent + "BB;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1.5;" +
            "-fx-effect: dropshadow(gaussian," + accent + ",12,0.25,0,0);"
        );
    }

    private StackPane buildEnergyBar() {
        Rectangle bg = new Rectangle(BAR_W, 10);
        bg.setFill(Color.web("#1a1a2e"));
        bg.setArcWidth(6); bg.setArcHeight(6);
        bg.setStroke(Color.web(accent + "55")); bg.setStrokeWidth(1);

        energyBarFg = new Rectangle(0, 10);
        energyBarFg.setArcWidth(6); energyBarFg.setArcHeight(6);
        energyBarFg.setFill(Color.web("#2ecc71")); 

        StackPane bar = new StackPane(bg, energyBarFg);
        StackPane.setAlignment(energyBarFg, Pos.CENTER_LEFT);
        bar.setMaxWidth(BAR_W);
        bar.setPrefHeight(12);
        return bar;
    }

    private void updateEnergyBar(int energy) {
        double pct = Math.max(0, Math.min(1.0, energy / (double) WIN_ENERGY));
        energyBarFg.setWidth(BAR_W * pct);
        
        if (pct < 0.33)      energyBarFg.setFill(Color.web("#e74c3c"));
        else if (pct < 0.66) energyBarFg.setFill(Color.web("#f39c12"));
        else                  energyBarFg.setFill(Color.web("#2ecc71"));
    }

    private void applyConfusionBorder(boolean confused) {
        String cc = "#f39c12";
        if (confused) {
            photoPane.setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                "-fx-background-radius: 36;" +
                "-fx-border-color: " + cc + ";" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 36;" +
                "-fx-effect: dropshadow(gaussian," + cc + ",18,0.55,0,0);"
            );
            boolean has = photoPane.getChildren().stream().anyMatch(n -> "confusion_badge".equals(n.getId()));
            if (!has) {
                Label badge = new Label("😵"); badge.setId("confusion_badge");
                badge.setStyle("-fx-font-size: 15px; -fx-background-color: rgba(0,0,0,0.65); -fx-background-radius: 8; -fx-padding: 1 3;");
                StackPane.setAlignment(badge, Pos.TOP_RIGHT);
                StackPane.setMargin(badge, new Insets(2, 2, 0, 0));
                photoPane.getChildren().add(badge);
            }
        } else {
            photoPane.setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                "-fx-background-radius: 36;" +
                "-fx-border-color: " + accent + "BB;" +
                "-fx-border-width: 2.5;" +
                "-fx-border-radius: 36;"
            );
            photoPane.getChildren().removeIf(n -> "confusion_badge".equals(n.getId()));
        }
    }

    private Label statLbl(String colour, int size, boolean bold) {
        Label l = new Label();
        l.setFont(font(F_INTER, size));
        l.setStyle("-fx-text-fill: " + colour + ";" + (bold ? " -fx-font-weight: bold;" : ""));
        l.setAlignment(Pos.CENTER); l.setMaxWidth(Double.MAX_VALUE);
        return l;
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

    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
}
