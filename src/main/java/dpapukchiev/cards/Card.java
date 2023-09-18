package dpapukchiev.cards;

import dpapukchiev.cost.Cost;
import dpapukchiev.effects.CardEffect;
import lombok.*;

@Setter
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

    public String report() {
        return "%s %s C(%s) %s".formatted(name, type.name(), cost.report(), effect.report());
    }
}
