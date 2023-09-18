package dpapukchiev.cards;

import dpapukchiev.cost.ManufacturedGoodCost;
import dpapukchiev.effects.ScienceEffect;

import java.util.List;

public class ScienceCard extends Card {

    public ScienceCard(
            String name,
            int requiredPlayersCount,
            List<ScienceSymbol> symbols,
            List<ManufacturedGood> manufacturedGoodCosts
    ) {
        super();
        this.type = CardType.SCIENCE;
        this.requiredPlayersCount = requiredPlayersCount;
        this.name = name;
        this.age = 1;
        this.effect = new ScienceEffect(symbols);
        this.cost = new ManufacturedGoodCost(manufacturedGoodCosts);
    }
}
