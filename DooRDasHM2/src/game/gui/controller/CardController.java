package game.gui.controller;

import game.engine.Board;
import game.engine.cards.Card;
import game.gui.view.popups.CardPopup;
import javafx.stage.Window;

import java.util.ArrayList;

/**
 * Handles the card-draw side of a turn:
 *  - detects whether a card was drawn this turn by comparing deck snapshots
 *  - shows the CardPopup when needed
 *  - provides shortEffect() used by CellController for the log line
 */
public class CardController {

    // ── card detection ────────────────────────────────────────────────────────

    /**
     * drawCard() always removes index 0 from Board.cards.
     * If the deck shrank by exactly 1, the drawn card was deckSnapshot.get(0).
     * Returns null if nothing was drawn (or the deck just reshuffled — rare edge case).
     */
    public Card detectDrawnCard(ArrayList<Card> deckSnapshot) {
        if (!deckSnapshot.isEmpty() && Board.cards.size() == deckSnapshot.size() - 1)
            return deckSnapshot.get(0);
        return null;
    }

    // ── popup ─────────────────────────────────────────────────────────────────

    /**
     * Shows the card-drawn blocking popup if a card was actually drawn.
     * Closing the popup does NOT terminate the game.
     */
    public void showPopupIfNeeded(Card drawn, boolean isPlayer, Window owner) {
        if (drawn != null)
            CardPopup.show(drawn, isPlayer, owner);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /** First sentence of the card's effect description — used in the compact log line. */
    public static String shortEffect(Card card) {
        String full = CardPopup.effectDescription(card);
        int nl  = full.indexOf('\n');
        int dot = full.indexOf('.');
        int cut = -1;
        if (nl  > 0) cut = nl;
        if (dot > 0) cut = (cut < 0) ? dot + 1 : Math.min(cut, dot + 1);
        return (cut > 0 && cut < full.length()) ? full.substring(0, cut).trim() : full;
    }
}
