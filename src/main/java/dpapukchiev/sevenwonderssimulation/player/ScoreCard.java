package dpapukchiev.sevenwonderssimulation.player;

import lombok.Builder;

import java.util.ArrayList;

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
    @Builder.Default
    private double scienceWildcards = 0;

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

        var max = Math.max(scienceCogwheels, Math.max(scienceCompasses, scienceTablets));
        var finalCogwheels = scienceCogwheels;
        var finalCompasses = scienceCompasses;
        var finalTablets = scienceTablets;

        if(max == scienceCogwheels){
            finalCogwheels += scienceWildcards;
        } else if(max == scienceCompasses){
            finalCompasses += scienceWildcards;
        } else if(max == scienceTablets){
            finalTablets += scienceWildcards;
        }

        score += Math.pow(finalCogwheels, 2);
        score += Math.pow(finalCompasses, 2);
        score += Math.pow(finalTablets, 2);

        return score;
    }

    public String report() {
        var report = new ArrayList<String>();
        if (victoryPoints > 0) {
            report.add("V:" + victoryPoints);
        }
        if (coins > 0) {
            report.add("$:" + getCoinsScore());
        }
        if (warPointsScore > 0) {
            report.add("W:" + warPointsScore);
        }
        if (getScienceScore() > 0) {
            report.add("S:" + getScienceScore());
        }
        return "Score: %s\n%s".formatted(getTotalScore(), String.join(" ", report));
    }
}
