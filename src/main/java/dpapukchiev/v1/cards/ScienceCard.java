package dpapukchiev.v1.cards;

import dpapukchiev.v1.cost.Cost;
import dpapukchiev.v1.cost.ManufacturedGoodCost;
import dpapukchiev.v1.effects.ScienceEffect;

import java.util.List;

public class ScienceCard extends Card {

    public ScienceCard(
            int age, String name,
            int requiredPlayersCount,
            List<ScienceSymbol> symbols,
            List<ManufacturedGood> manufacturedGoodCosts
    ) {
        super();
        this.type = CardType.SCIENCE;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = new ScienceEffect(symbols);
        this.cost = new ManufacturedGoodCost(manufacturedGoodCosts);
    }

    public ScienceCard(
            int age, String name,
            int requiredPlayersCount,
            List<ScienceSymbol> symbols,
            Cost cost
    ) {
        super();
        this.type = CardType.SCIENCE;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = age;
        this.effect = new ScienceEffect(symbols);
        this.cost = cost;
    }
}
