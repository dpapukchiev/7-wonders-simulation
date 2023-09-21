package dpapukchiev.sevenwonderssimulation.cards;

import dpapukchiev.sevenwonderssimulation.cost.Cost;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
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
        return "%s %s %s %s".formatted(type.name(), name, cost.report(), effect.report());
    }
}
