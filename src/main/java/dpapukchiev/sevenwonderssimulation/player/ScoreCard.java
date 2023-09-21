package dpapukchiev.sevenwonderssimulation.player;

import lombok.Builder;

@Builder
public class ScoreCard {
    @Builder.Default
    private double victoryPoints    = 0;
    @Builder.Default
    private double coins            = 0;
    @Builder.Default
    private double warPointsScore   = 0;
    @Builder.Default
    private double scienceCogwheels = 0;
    @Builder.Default
    private double scienceCompasses = 0;
    @Builder.Default
    private double scienceTablets   = 0;

    public double getTotalScore() {
        return getScienceScore() + getCoinsScore() + warPointsScore + victoryPoints;
    }

    public double getCoinsScore() {
        return Math.floor(this.coins / 3);
    }

    public double getScienceScore() {
        var score = 0d;
        if (scienceCogwheels > 0 && scienceCompasses > 0 && scienceTablets > 0) {
            score += 7;
        }

        score += Math.pow(scienceCogwheels, 2);
        score += Math.pow(scienceCompasses, 2);
        score += Math.pow(scienceTablets, 2);

        return score;
    }
}
