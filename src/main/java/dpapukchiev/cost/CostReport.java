package dpapukchiev.cost;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
@Builder
public class CostReport {
    @Builder.Default
    private String  resourcesIncluded = "";
    @Builder.Default
    private boolean affordable        = false;
    @Builder.Default
    private double  toPayBank       = 0d;
    @Builder.Default
    private double  toPayLeft       = 0d;
    @Builder.Default
    private double  toPayRight      = 0d;

    public CostReport merge(CostReport costReport) {
        if (costReport == null) {
            return this;
        }
        if(resourcesIncluded.isEmpty()) {
            resourcesIncluded = costReport.resourcesIncluded;
        }
        if (!Objects.equals(costReport.getResourcesIncluded(), resourcesIncluded)) {
            resourcesIncluded += "," + costReport.resourcesIncluded;
        }
        return CostReport.builder()
                .resourcesIncluded(resourcesIncluded)
                .affordable(affordable && costReport.affordable)
                .toPayBank(toPayBank + costReport.toPayBank)
                .toPayLeft(toPayLeft + costReport.toPayLeft)
                .toPayRight(toPayRight + costReport.toPayRight)
                .build();
    }
}
