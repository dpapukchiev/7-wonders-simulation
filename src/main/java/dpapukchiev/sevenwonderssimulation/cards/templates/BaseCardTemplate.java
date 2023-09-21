package dpapukchiev.sevenwonderssimulation.cards.templates;

import dpapukchiev.sevenwonderssimulation.cost.CoinCost;
import dpapukchiev.sevenwonderssimulation.cost.ComplexResourceCost;
import dpapukchiev.sevenwonderssimulation.cost.Cost;
import dpapukchiev.sevenwonderssimulation.cost.FreeToPlayCost;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;

import java.util.List;

public class BaseCardTemplate {
    protected Cost createCost(List<RawMaterial> rawMaterialsCost, List<ManufacturedGood> manufacturedGoodsCost) {
        return rawMaterialsCost.isEmpty() &&
                manufacturedGoodsCost.isEmpty() ?
                getAlternativeCost() :
                ComplexResourceCost.builder()
                        .rawMaterialList(rawMaterialsCost)
                        .manufacturedGoodsList(manufacturedGoodsCost)
                        .build();
    }

    private Cost getAlternativeCost() {
        return getCoinCost() == 0 ? FreeToPlayCost.newInstance() : CoinCost.of(getCoinCost());
    }

    protected int getCoinCost() {
        return 0;
    }
}
