package dpapukchiev.cost;

import dpapukchiev.cards.RawMaterial;
import dpapukchiev.game.TurnContext;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@AllArgsConstructor
public class RawMaterialCost implements Cost {
    private final List<RawMaterial> rawMaterialsList;

    @Override
    public boolean canBuild(TurnContext turnContext) {
        var materialsNeeded = rawMaterialsList.stream()
                .collect(groupingBy(RawMaterial::name));

        return materialsNeeded.entrySet().stream().allMatch(rm -> {
            var requiredCount = rm.getValue().size();
            var currentCount = turnContext.getPlayer().getRawMaterialCount(RawMaterial.valueOf(rm.getKey()));
            return currentCount >= requiredCount;
        });
    }

    @Override
    public void applyCost(TurnContext turnContext) {

    }

    @Override
    public String report() {
        return "RM: " + rawMaterialsList.stream().map(RawMaterial::name)
                .map(rm -> rm.substring(0,1))
                .collect(Collectors.joining());
    }
}
