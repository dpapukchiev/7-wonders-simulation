package dpapukchiev.sevenwonderssimulation.wonder;

import dpapukchiev.sevenwonderssimulation.cost.CostReport;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
public class WonderContext {
    private CityName          cityName;
    @Builder.Default
    private List<WonderStage> wonderStages = new ArrayList<>();

    public Optional<Pair<WonderStage, CostReport>> getNextAffordableWonderStage(TurnContext turnContext) {
        return wonderStages.stream()
                .filter(ws -> !ws.isBuilt())
                .map(ws -> Pair.of(ws, ws.getCost().generateCostReport(turnContext)))
                .min(Comparator.comparingDouble(p -> p.getLeft().getStageNumber()))
                .filter(p -> p.getRight().isAffordable());
    }

    public long getBuiltStageCount() {
        return wonderStages.stream()
                .filter(WonderStage::isBuilt)
                .count();
    }

    public String report() {
        return String.format("W(%s/%s)", wonderStages.stream().filter(WonderStage::isBuilt).count(), wonderStages.size());
    }
}
