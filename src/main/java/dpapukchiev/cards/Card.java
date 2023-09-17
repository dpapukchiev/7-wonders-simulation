package dpapukchiev.cards;

import dpapukchiev.cost.Cost;
import dpapukchiev.effects.CardEffect;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Card {
    @ToString.Include
    private String name;
    private int requiredPlayersCount;
    private int age;
    private Cost cost;
    private CardType type;
    private CardEffect effect;
}
