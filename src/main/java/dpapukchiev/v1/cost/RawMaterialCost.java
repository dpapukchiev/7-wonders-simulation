package dpapukchiev.v1.cost;

import dpapukchiev.v1.cards.RawMaterial;
import dpapukchiev.v1.player.Player;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class RawMaterialCost extends BaseResourceCost<RawMaterial> {

    public RawMaterialCost(List<RawMaterial> rawMaterialsList) {
        super(rawMaterialsList);
    }

    @Override
    public double getTradingPriceRight(Player player, String neededMaterial) {
        return player.getTradingPriceRight(RawMaterial.valueOf(neededMaterial));
    }

    @Override
    public double getTradingPriceLeft(Player player, String neededMaterial) {
        return player.getTradingPriceLeft(RawMaterial.valueOf(neededMaterial));
    }

    @Override
    public double getAvailableResourcesRight(Player player, String neededMaterial) {
        return player.getRightPlayer().getRawMaterialCount(RawMaterial.valueOf(neededMaterial));
    }

    @Override
    public double getAvailableResourcesLeft(Player player, String neededMaterial) {
        return player.getLeftPlayer().getRawMaterialCount(RawMaterial.valueOf(neededMaterial));
    }

    @Override
    public double getCurrentCount(Player player, String neededMaterial) {
        return player.getRawMaterialCount(RawMaterial.valueOf(neededMaterial)) +
                player.getRawMaterialCountWildcard(RawMaterial.valueOf(neededMaterial));
    }

    @Override
    public String report() {
        return "RM: " + resourceList.stream().map(RawMaterial::name)
                .map(rm -> rm.substring(0, 1))
                .collect(Collectors.joining());
    }
}
