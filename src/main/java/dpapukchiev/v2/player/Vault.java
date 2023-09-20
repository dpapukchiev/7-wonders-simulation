package dpapukchiev.v2.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vault {
    @Builder.Default
    private List<WarPoint> warPoints     = new ArrayList<>();
    @Builder.Default
    private double            victoryPoints = 0;
    @Builder.Default
    private double            shields       = 0;
    @Builder.Default
    private double            coins         = 3;

    public void addWarPoint(WarPoint warPoint) {
        warPoints.add(warPoint);
    }

    public double getWarPoints() {
        return warPoints.stream().mapToInt(WarPoint::getValue).sum();
    }

    public void addVictoryPoints(double victoryPoints) {
        this.victoryPoints += victoryPoints;
    }

    public void addShields(double shields) {
        this.shields += shields;
    }

    public void addCoins(double coins) {
        this.coins += coins;
    }

    public void removeCoins(double coins) {
        this.coins -= coins;
    }
}
