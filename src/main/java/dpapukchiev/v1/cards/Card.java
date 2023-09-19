package dpapukchiev.v1.cards;

import dpapukchiev.v1.cost.Cost;
import dpapukchiev.v1.effects.CardEffect;
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
    protected String       name;
    @Builder.Default
    protected List<String> freeUpgrades = new ArrayList<>();
    protected int          requiredPlayersCount;
    protected int      age;
    protected Cost     cost;
    protected CardType type;
    @ToString.Include
    protected CardEffect   effect;

    public Card withFreeUpgrades(List<String> freeUpgrades) {
        this.freeUpgrades = freeUpgrades;
        return this;
    }

    public String report() {
        return "%s %s C(%s) %s".formatted(type.name(), name, cost.report(), effect.report());
    }
}