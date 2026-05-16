package game.gui.view.popups;

import game.engine.cards.Card;
import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Card-drawn popup: card title, rounded photo with rarity glow, effect description, Got it! button.
 *
 * Rarity glow (fewer copies in pile = rarer = more valuable colour):
 *   Mega Drain (×1)       → gold   #f1c40f
 *   Sneaky/Code/Total (×2) → orange #e67e22
 *   Small/2319/Mind (×3)  → purple #9b59b6
 *   Position Swap (×4)    → green  #27ae60
 *   Super Shield (×5)     → blue   #3498db
 */
public final class CardPopup {

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    public static void show(Card card, boolean drawnByPlayer, Window owner) {
        String rarityColor = rarityColor(card);
        String rarityLabel = rarityLabel(card);

        Stage popup = new Stage();
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) popup.initOwner(owner);
        popup.setResizable(false);

        // ── card image ────────────────────────────────────────────────────────
        StackPane imgPane = new StackPane();
        imgPane.setPrefSize(160, 160);
        imgPane.setMaxSize(160, 160);

        Image img = ResourceLoader.loadImage(imageKey(card), 150, 150);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(150); iv.setFitHeight(150); iv.setPreserveRatio(false);
            Rectangle clip = new Rectangle(150, 150);
            clip.setArcWidth(22); clip.setArcHeight(22);
            iv.setClip(clip);
            imgPane.getChildren().add(iv);
        } else {
            Label fb = new Label("🃏");
            fb.setStyle("-fx-font-size: 60px;");
            imgPane.getChildren().add(fb);
        }

        // Glowing border frame around image
        Rectangle border = new Rectangle(154, 154);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.web(rarityColor));
        border.setStrokeWidth(2.5);
        border.setArcWidth(24); border.setArcHeight(24);
        DropShadow glow = new DropShadow(24, Color.web(rarityColor, 0.80));
        glow.setSpread(0.25);
        border.setEffect(glow);
        imgPane.getChildren().add(border);

        // ── rarity badge ──────────────────────────────────────────────────────
        Label rarityBadge = new Label(rarityLabel);
        rarityBadge.setFont(font(F_PIXEL, 7));
        rarityBadge.setStyle(
            "-fx-text-fill: " + rarityColor + ";" +
            "-fx-background-color: rgba(0,0,0,0.65);" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 3 8;" +
            "-fx-border-color: " + rarityColor + "66;" +
            "-fx-border-radius: 6;" +
            "-fx-border-width: 1;"
        );

        // ── card title ────────────────────────────────────────────────────────
        Label titleLbl = new Label(card.getName().toUpperCase());
        titleLbl.setFont(font(F_BANGERS, 30));
        titleLbl.setStyle("-fx-text-fill: white;");

        // ── drawn by label ────────────────────────────────────────────────────
        Label drawnLbl = new Label((drawnByPlayer ? "You drew" : "Opponent drew") + " a card!");
        drawnLbl.setFont(font(F_INTER, 12));
        drawnLbl.setStyle("-fx-text-fill: #95a5a6;");

        // ── effect description ────────────────────────────────────────────────
        Label descLbl = new Label(effectDescription(card));
        descLbl.setFont(font(F_INTER, 13));
        descLbl.setStyle("-fx-text-fill: #ecf0f1; -fx-line-spacing: 2;");
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(330);
        descLbl.setAlignment(Pos.CENTER);
        descLbl.setTextAlignment(TextAlignment.CENTER);

        // ── got it button ─────────────────────────────────────────────────────
        Button gotItBtn = new Button("GOT IT!");
        gotItBtn.setFont(font(F_PIXEL, 9));
        gotItBtn.setPrefSize(180, 42);
        String btnBase  = "-fx-background-color: " + rarityColor + "; -fx-text-fill: #0d0d1a; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(gaussian," + rarityColor + ",14,0.5,0,0);";
        String btnDim   = "-fx-background-color: " + rarityColor + "BB; -fx-text-fill: #0d0d1a; -fx-background-radius: 10; -fx-cursor: hand;";
        gotItBtn.setStyle(btnBase);
        gotItBtn.setOnMouseEntered(e -> gotItBtn.setStyle(btnDim));
        gotItBtn.setOnMouseExited(e  -> gotItBtn.setStyle(btnBase));
        gotItBtn.setOnAction(e -> popup.getScene().getWindow().hide());

        Region sep = new Region(); sep.setPrefHeight(1); sep.setMaxWidth(320);
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.12);");

        VBox content = new VBox(12, drawnLbl, imgPane, rarityBadge, titleLbl, sep, descLbl, gotItBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28, 32, 28, 32));

        Rectangle clip = new Rectangle(420, 580);
        clip.setArcWidth(24); clip.setArcHeight(24);

        StackPane root = new StackPane(content);
        root.setClip(clip);
        root.setStyle(
            "-fx-background-color: #0d0d1a;" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: " + rarityColor + "66;" +
            "-fx-border-radius: 18;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian," + rarityColor + ",30,0.30,0,0);"
        );

        Scene scene = new Scene(root, 420, 580);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    public static String effectDescription(Card card) {
        String n = card.getName();
        if (n.contains("Position"))      return "Swap places with your opponent — but only if you're behind them. No advantage? No swap.";
        if (n.contains("Contamination")) return "You're sent all the way back to Cell 0. Hope you enjoyed the progress.";
        if (n.contains("2319"))          return "Your opponent gets sent back to Cell 0. Watch them suffer the walk of shame.";
        if (n.contains("Small"))         return "Steal 50 energy from your opponent. Small gains — but they add up.";
        if (n.contains("Sneaky"))        return "Steal 100 energy from your opponent. Clean, quiet, effective.";
        if (n.contains("Mega"))          return "Drain 150 energy from your opponent. The most devastating steal on the board.";
        if (n.contains("Shield"))        return "Your entire team is shielded from the next energy loss. One hit, then it's gone.";
        if (n.contains("Mind"))          return "Both players have their roles swapped for 2 turns. Watch which doors you land on!";
        if (n.contains("Total"))         return "Full chaos — both roles swapped for 3 turns. Good luck keeping track.";
        return card.getName() + " takes effect.";
    }

    private static String imageKey(Card card) {
        String n = card.getName();
        if (n.contains("2319"))          return "2319_Alert";
        if (n.contains("Contamination")) return "Contamination_Code";
        if (n.contains("Position"))      return "Position_Swap";
        if (n.contains("Shield"))        return "Super_Shield";
        if (n.contains("Small"))         return "Small_Snatcher";
        if (n.contains("Sneaky"))        return "Sneaky_Thief";
        if (n.contains("Mega"))          return "Mega_Drain";
        if (n.contains("Mind"))          return "Mind_Scramble";
        if (n.contains("Total"))         return "Total_Confusion";
        return "Card_Cell";
    }

    private static String rarityColor(Card card) {
        String n = card.getName();
        if (n.contains("Mega"))                                               return "#f1c40f"; // gold — rarest ×1
        if (n.contains("Sneaky") || n.contains("Contamination") || n.contains("Total")) return "#e67e22"; // orange ×2
        if (n.contains("Small")  || n.contains("2319")          || n.contains("Mind"))  return "#9b59b6"; // purple ×3
        if (n.contains("Position"))                                           return "#27ae60"; // green ×4
        return "#3498db"; // blue — most common ×5 (Shield)
    }

    private static String rarityLabel(Card card) {
        String n = card.getName();
        if (n.contains("Mega"))                                               return "LEGENDARY";
        if (n.contains("Sneaky") || n.contains("Contamination") || n.contains("Total")) return "RARE";
        if (n.contains("Small")  || n.contains("2319")          || n.contains("Mind"))  return "UNCOMMON";
        if (n.contains("Position"))                                           return "COMMON";
        return "BASIC";
    }

    private static Font font(String path, double size) { return ResourceLoader.font(path, size); }
    private CardPopup() {}
}
