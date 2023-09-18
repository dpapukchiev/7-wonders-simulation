package dpapukchiev.effects;

import dpapukchiev.cards.ScienceSymbol;

import java.util.List;

public class ScienceEffect extends CardEffect {
    public ScienceEffect(
            List<ScienceSymbol> symbolList
    ) {
        super();
        this.maxUsages = 1;
        this.effectUsageType = EffectUsageType.END_OF_GAME;
        this.providedScienceSymbols = symbolList;
        this.wildcardScienceSymbol = symbolList.stream().distinct().count() > 1;
    }

}
