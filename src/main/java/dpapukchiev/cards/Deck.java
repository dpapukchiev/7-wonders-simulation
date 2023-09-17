package dpapukchiev.cards;

import dpapukchiev.cost.FreeToPlayCost;
import dpapukchiev.effects.RawMaterialEffect;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.simulation.ModelElement;
import jsl.utilities.random.rvariable.NormalRV;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

public class Deck {
    private final RandomVariable cardDistribution;
    public List<Card> allCards = new ArrayList<>();
    public List<Card> discardedCards = new ArrayList<>();

    public Deck(ModelElement parent) {
        cardDistribution = new RandomVariable(parent, new NormalRV());
    }

    public void resetDeck(int playerCount) {
        allCards.clear();
        discardedCards.clear();

        var effectOneStone = new RawMaterialEffect(List.of(
                RawMaterial.STONE
        ));
        var freeToPlayCost = new FreeToPlayCost();

        // TODO: replace with hard coded cards
        for (int i = 1; i <= 3; i++) {
            for (int j = 0; j < 7; j++) {
                for (int k = 0; k < 7; k++) {
                    allCards.add(Card.builder()
                            .name("Ziegelei-" + j + "-" + k)
                            .requiredPlayersCount(3)
                            .type(CardType.RAW_MATERIAL)
                            .effect(effectOneStone)
                            .cost(freeToPlayCost)
                            .age(i)
                            .build()
                    );
                }
            }
        }

        allCards = allCards.stream()
                .filter(card -> card.getRequiredPlayersCount() >= playerCount)
                .collect(Collectors.toList());
    }

    public HandOfCards prepareHandOfCards(int age) {
        var cards = new ArrayList<Card>();
        var availableCards = allCards.stream()
                .filter(card -> card.getAge() == age)
                .collect(Collectors.toList());

        for (int i = 0; i < 7; i++) {
            var card = randomlySelect(availableCards, cardDistribution.getStreamNumber());
            cards.add(card);
            availableCards.remove(card);
            allCards.remove(card);
        }


        return new HandOfCards(UUID.randomUUID(), cards);
    }
}
