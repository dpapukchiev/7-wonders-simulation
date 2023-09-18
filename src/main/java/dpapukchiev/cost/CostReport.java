package dpapukchiev.cost;

import lombok.Builder;
import lombok.Getter;

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
        return CostReport.builder()
                .affordable(affordable && costReport.affordable)
                .toPayBank(toPayBank + costReport.toPayBank)
                .toPayLeft(toPayLeft + costReport.toPayLeft)
                .toPayRight(toPayRight + costReport.toPayRight)
                .build();
    }
}
