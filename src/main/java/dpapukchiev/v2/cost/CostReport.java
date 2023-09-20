package dpapukchiev.v2.cost;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@Builder(toBuilder = true)
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


    public CostReport setUnaffordable(){
        affordable = false;
        toPayBank = 0;
        toPayLeft = 0;
        toPayRight = 0;
        return this;
    }

    public void addToPayBank(double toPayBank) {
        this.toPayBank += toPayBank;
    }

    public void addToPayLeft(double toPayLeft) {
        this.toPayLeft += toPayLeft;
    }

    public void addToPayRight(double toPayRight) {
        this.toPayRight += toPayRight;
    }
    public double getToPayTotal() {
        return toPayBank + toPayLeft + toPayRight;
    }

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
