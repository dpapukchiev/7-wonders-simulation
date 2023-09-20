package dpapukchiev.v2.cards;

import dpapukchiev.v2.cost.Cost;
import dpapukchiev.v2.effects.core.Effect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Card {
    @ToString.Include
    protected CardName     name;
    @Builder.Default
    protected List<String> freeUpgrades = new ArrayList<>();
    protected int          age;
    protected int          requiredPlayersCount;
    protected Cost         cost;
    protected CardType     type;
    @ToString.Include
    protected Effect       effect;

    public String report() {
        return "%s %s C(%s) %s".formatted(type.name(), name, cost.report(), effect.report());
    }
}
