package dpapukchiev.v1.cost;

import dpapukchiev.v1.cards.ManufacturedGood;
import dpapukchiev.v1.player.Player;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ManufacturedGoodCost extends BaseResourceCost<ManufacturedGood> {

    public ManufacturedGoodCost(List<ManufacturedGood> manufacturedGoodList) {
        super(manufacturedGoodList);
    }

    @Override
    public double getTradingPriceRight(Player player, String neededMaterial) {
        return player.getTradingPriceRight(ManufacturedGood.valueOf(neededMaterial));
    }

    @Override
    public double getTradingPriceLeft(Player player, String neededMaterial) {
        return player.getTradingPriceLeft(ManufacturedGood.valueOf(neededMaterial));
    }

    @Override
    public double getAvailableResourcesRight(Player player, String neededMaterial) {
        return player.getRightPlayer().getManufacturedGoodCount(ManufacturedGood.valueOf(neededMaterial));
    }

    @Override
    public double getAvailableResourcesLeft(Player player, String neededMaterial) {
        return player.getLeftPlayer().getManufacturedGoodCount(ManufacturedGood.valueOf(neededMaterial));
    }

    @Override
    public double getCurrentCount(Player player, String neededMaterial) {
        return player.getManufacturedGoodCount(ManufacturedGood.valueOf(neededMaterial)) +
                player.getManufacturedGoodCountWildcard(ManufacturedGood.valueOf(neededMaterial));
    }

    @Override
    public String report() {
        return "MG: " + resourceList.stream().map(ManufacturedGood::name)
                .map(rm -> rm.substring(0, 1))
                .collect(Collectors.joining());
    }
}
