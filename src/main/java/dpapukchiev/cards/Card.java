package dpapukchiev.cards;

import dpapukchiev.cost.Cost;
import dpapukchiev.effects.CardEffect;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Card {
    @ToString.Include
    protected String name;
    protected int requiredPlayersCount;
    protected int age;
    protected Cost cost;
    protected CardType type;
    @ToString.Include
    protected CardEffect effect;
}
