package dpapukchiev.v2.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vault {
    @Builder.Default
    private List<WarPoint> warPoints     = new ArrayList<>();
    @Builder.Default
    private int            victoryPoints = 0;
    @Builder.Default
    private int            shields       = 0;
    @Builder.Default
    private int            coins         = 3;

    public void addWarPoint(WarPoint warPoint) {
        warPoints.add(warPoint);
    }

    public int getWarPoints() {
        return warPoints.stream().mapToInt(WarPoint::getValue).sum();
    }

    public void addVictoryPoints(int victoryPoints) {
        this.victoryPoints += victoryPoints;
    }

    public void addShields(int shields) {
        this.shields += shields;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }
}
