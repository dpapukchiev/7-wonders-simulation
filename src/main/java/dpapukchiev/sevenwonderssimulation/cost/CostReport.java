package dpapukchiev.sevenwonderssimulation.cost;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
public class CostReport {
    @Builder.Default
    private String  resourcesIncluded = "";
    @Builder.Default
    private boolean affordable        = false;
    @Builder.Default
    private double  toPayBank         = 0d;
    @Builder.Default
    private double  toPayLeft         = 0d;
    @Builder.Default
    private double  toPayRight        = 0d;

    public CostReport setUnaffordable() {
        affordable = false;
        toPayBank = 0;
        toPayLeft = 0;
        toPayRight = 0;
        return this;
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

}
