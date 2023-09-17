package dpapukchiev.cost;

import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.game.TurnContext;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ResourceCost implements Cost {
    private final List<ManufacturedGood> manufacturedGoodList;
    private final List<RawMaterial> rawMaterialsList;
    @Override
    public boolean canBuild(TurnContext turnContext) {
        var builtCards = turnContext.getPlayer().getBuiltCards();

        return true;
    }
}
