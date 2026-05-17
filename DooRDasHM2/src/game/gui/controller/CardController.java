package game.gui.controller;

import game.engine.Board;
import game.engine.cards.Card;
import game.gui.view.popups.CardPopup;
import javafx.stage.Window;

import java.util.ArrayList;

public class CardController {

   
    public Card detectDrawnCard(ArrayList<Card> deckSnapshot) {
        if (!deckSnapshot.isEmpty() && Board.cards.size() == deckSnapshot.size() - 1)
            return deckSnapshot.get(0);
        return null;
    }

   
    public void showPopupIfNeeded(Card drawn, boolean isPlayer, Window owner) {
        if (drawn != null)
            CardPopup.show(drawn, isPlayer, owner);
    }

  
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
