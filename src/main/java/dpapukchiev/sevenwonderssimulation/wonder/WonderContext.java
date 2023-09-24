package dpapukchiev.sevenwonderssimulation.wonder;

import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectState;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class WonderContext {
    private CityName          cityName;
    private String            side;
    @Builder.Default
    private List<WonderStage> wonderStages = new ArrayList<>();

    public Optional<WonderStage> getNextWonderStage(TurnContext turnContext) {
        return wonderStages.stream()
                .filter(ws -> !ws.isBuilt())
                .min(Comparator.comparingDouble(WonderStage::getStageNumber))
                .filter(ws -> ws.getCost().generateCostReport(turnContext).isAffordable());
    }

    public long getBuiltStageCount() {
        return wonderStages.stream()
                .filter(WonderStage::isBuilt)
                .count();
    }

    public String getName() {
        return "%s-%s".formatted(cityName.name(), side);
    }

    public String report() {
        var report = new ArrayList<String>();
        report.add("W(%s %s/%s)".formatted(
                side,
                wonderStages.stream()
                        .filter(WonderStage::isBuilt)
                        .count(),
                wonderStages.size()
        ));
        var builtWonderStagesEffects = getBuiltWonderStagesEffects();
        if (!builtWonderStagesEffects.isEmpty()) {
            report.add("wonder efx: \n%s".formatted(builtWonderStagesEffects
                    .stream()
                    .map(e -> "%s-%s".formatted(e.getState().equals(EffectState.AVAILABLE) ? "1" : "0", e.report()))
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.joining(" "))));
        }
        return String.join("\n", report);
    }

    private List<Effect> getBuiltWonderStagesEffects() {
        return wonderStages.stream()
                .filter(WonderStage::isBuilt)
                .map(WonderStage::getEffect)
                .toList();
    }
}
