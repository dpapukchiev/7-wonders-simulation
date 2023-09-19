package dpapukchiev.v2.cards;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class Deck {
    @Builder.Default
    private List<Card> discardedCards = new ArrayList<>();

    public void discard(Card card) {
        discardedCards.add(card);
    }
}
