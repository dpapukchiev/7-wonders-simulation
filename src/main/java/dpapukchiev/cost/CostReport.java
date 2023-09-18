package dpapukchiev.cost;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
@Builder
public class CostReport {
    @Builder.Default
    private String  missingResource = "";
    @Builder.Default
    private boolean affordable      = false;
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
        if(missingResource.isEmpty()) {
            missingResource = costReport.missingResource;
        }
        if (!Objects.equals(costReport.getMissingResource(), missingResource)) {
           missingResource += "," + costReport.missingResource;
        }
        return CostReport.builder()
                .missingResource(missingResource)
                .affordable(affordable && costReport.affordable)
                .toPayBank(toPayBank + costReport.toPayBank)
                .toPayLeft(toPayLeft + costReport.toPayLeft)
                .toPayRight(toPayRight + costReport.toPayRight)
                .build();
    }
}
