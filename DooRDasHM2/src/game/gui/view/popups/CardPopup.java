package game.gui.view.popups;

import game.engine.cards.*;
import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Modal popup that appears whenever a monster lands on a Card Cell.
 * Shows the card name, type badge, one-line effect description, and a dismiss button.
 * Closing the popup does NOT terminate the game.
 */
public class CardPopup {

    // ── colour palette (matches GameView) ───────────────────────────────────
    private static final String BG_DARK   = "#0d0d1a";
    private static final String GOLD      = "#f1c40f";
    private static final String TEXT_MAIN = "#ecf0f1";
    private static final String TEXT_DIM  = "#95a5a6";
    private static final String RED_BTN   = "#c0392b";
    private static final String RED_HOVER = "#e74c3c";

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    // ── public entry point ───────────────────────────────────────────────────

    /**
     * Derive all display info from the Card object itself and show the popup.
     *
     * @param card          the card that was drawn (must not be null)
     * @param drawnByPlayer true if the current player drew the card, false if opponent did
     * @param owner         parent window for modality (may be null)
     */
    public static void show(Card card, boolean drawnByPlayer, Window owner) {
        String emoji       = emojiFor(card);
        String typeBadge   = typeLabel(card);
        String typeBgColor = typeBadgeColor(card);
        String effect      = effectDescription(card);
        String drawer      = drawnByPlayer ? "YOU drew a card!" : "OPPONENT drew a card!";

        Stage popup = new Stage();
        if (owner != null) popup.initOwner(owner);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setResizable(false);
        popup.setTitle("Card Drawn");

        // ── "drawn by" header ────────────────────────────────────────────────
        Label drawerLbl = new Label(drawer);
        drawerLbl.setFont(font(F_PIXEL, 8));
        drawerLbl.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        drawerLbl.setAlignment(Pos.CENTER);
        drawerLbl.setMaxWidth(Double.MAX_VALUE);

        // ── big card emoji ────────────────────────────────────────────────────
        Label iconLbl = new Label(emoji);
        iconLbl.setStyle("-fx-font-size: 52px;");

        // ── card name ─────────────────────────────────────────────────────────
        Label nameLbl = new Label(card.getName());
        nameLbl.setFont(font(F_BANGERS, 32));
        nameLbl.setStyle("-fx-text-fill: " + GOLD + ";");
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setMaxWidth(Double.MAX_VALUE);
        nameLbl.setTextAlignment(TextAlignment.CENTER);

        // ── type badge ────────────────────────────────────────────────────────
        Label typeLbl = new Label(typeBadge);
        typeLbl.setFont(font(F_INTER, 11));
        typeLbl.setStyle(
            "-fx-text-fill: white;" +
            "-fx-background-color: " + typeBgColor + ";" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 3 10;"
        );

        // ── separator ─────────────────────────────────────────────────────────
        Separator sep = new Separator();
        sep.setMaxWidth(320);
        sep.setStyle("-fx-background-color: rgba(155,89,182,0.40);");

        // ── effect description ────────────────────────────────────────────────
        Label effectLbl = new Label(effect);
        effectLbl.setFont(font(F_INTER, 14));
        effectLbl.setStyle("-fx-text-fill: " + TEXT_MAIN + ";");
        effectLbl.setWrapText(true);
        effectLbl.setMaxWidth(340);
        effectLbl.setAlignment(Pos.CENTER);
        effectLbl.setTextAlignment(TextAlignment.CENTER);

        // ── dismiss button ────────────────────────────────────────────────────
        Button okBtn = new Button("GOT IT!");
        okBtn.setFont(font(F_PIXEL, 9));
        okBtn.setPrefSize(160, 44);
        styleBtn(okBtn, RED_BTN);
        okBtn.setOnMouseEntered(e -> styleBtn(okBtn, RED_HOVER));
        okBtn.setOnMouseExited (e -> styleBtn(okBtn, RED_BTN));
        okBtn.setOnAction(e -> popup.close());

        // ── layout ────────────────────────────────────────────────────────────
        VBox content = new VBox(10,
            drawerLbl, iconLbl, nameLbl, typeLbl, sep, effectLbl, okBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28, 32, 28, 32));

        // clipped rounded background
        Rectangle clip = new Rectangle(420, 300);
        clip.setArcWidth(24);
        clip.setArcHeight(24);

        StackPane root = new StackPane(content);
        root.setClip(clip);
        root.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(155,89,182,0.70);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian,#6c3483,24,0.40,0,0);"
        );

        Scene scene = new Scene(root, 420, 300);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
        // showAndWait returns only after the user closes the popup — the game loop continues.
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static String emojiFor(Card card) {
        if (card instanceof SwapperCard)     return "🔄";
        if (card instanceof EnergyStealCard) return "⚡";
        if (card instanceof StartOverCard)   return card.isLucky() ? "🎯" : "💀";
        if (card instanceof ShieldCard)      return "🛡";
        if (card instanceof ConfusionCard)   return "😵";
        return "🃏";
    }

    /** Short human-readable type label shown in the badge. */
    static String typeLabel(Card card) {
        if (card instanceof SwapperCard)     return "POSITION SWAP";
        if (card instanceof EnergyStealCard) return "ENERGY STEAL";
        if (card instanceof StartOverCard)   return card.isLucky() ? "START OVER  ✦  LUCKY" : "START OVER  ✦  UNLUCKY";
        if (card instanceof ShieldCard)      return "SHIELD";
        if (card instanceof ConfusionCard)   return "CONFUSION";
        return "CARD";
    }

    /** Accent colour for the type badge. */
    private static String typeBadgeColor(Card card) {
        if (card instanceof SwapperCard)     return "#2980b9";
        if (card instanceof EnergyStealCard) return "#e67e22";
        if (card instanceof StartOverCard)   return card.isLucky() ? "#27ae60" : "#c0392b";
        if (card instanceof ShieldCard)      return "#8e44ad";
        if (card instanceof ConfusionCard)   return "#d35400";
        return "#555";
    }

    /**
     * One-line human-readable description of what the card does.
     * Uses the typed getters so the numbers are always accurate.
     */
    public static String effectDescription(Card card) {
        if (card instanceof SwapperCard) {
            return "Swap positions with your opponent — but only if you're behind!";
        }
        if (card instanceof EnergyStealCard) {
            int amt = ((EnergyStealCard) card).getEnergy();
            return "Steal " + amt + " energy from your opponent.\n"
                 + "(Opponent's shield will block this entirely.)";
        }
        if (card instanceof StartOverCard) {
            return card.isLucky()
                ? "Opponent is sent back to cell 0! Advantage: YOU."
                : "YOU are sent back to cell 0. Ouch.";
        }
        if (card instanceof ShieldCard) {
            return "Your team gains a shield that blocks the next\nnegative energy effect.";
        }
        if (card instanceof ConfusionCard) {
            int dur = ((ConfusionCard) card).getDuration();
            return "Both monsters swap roles for " + dur + " turn" + (dur > 1 ? "s" : "") + "!\n"
                 + "Door interactions will be reversed during this time.";
        }
        return card.getDescription();
    }

    // ── JavaFX utilities ─────────────────────────────────────────────────────

    private static Font font(String path, double size) {
        return ResourceLoader.font(path, size);
    }

    private static void styleBtn(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + color + ",14,0.50,0,0);"
        );
    }
}
