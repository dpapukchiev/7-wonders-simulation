package dpapukchiev.sevenwonderssimulation.player;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreCardTest {

    @CsvSource({
        "1,1,0,0,0,0,1",
        "1,1,1,0,0,0,2",
        "1,1,1,1,0,0,3",
        "1,1,1,1,1,0,4",
        "1,1,1,1,1,1,12",

        // coins
        "0,1,0,0,0,0,0",
        "0,2,0,0,0,0,0",
        "0,3,0,0,0,0,1",
        "0,4,0,0,0,0,1",
        "0,5,0,0,0,0,1",
        "0,6,0,0,0,0,2",

        // war points
        "0,0,1,0,0,0,1",
        "0,0,2,0,0,0,2",
        "0,0,3,0,0,0,3",

        // victory points
        "1,0,0,0,0,0,1",
        "2,0,0,0,0,0,2",
        "3,0,0,0,0,0,3",

        // science cogwheels
        "0,0,0,1,0,0,1",
        "0,0,0,2,0,0,4",
        "0,0,0,3,0,0,9",
        "0,0,0,4,0,0,16",

        // science compasses
        "0,0,0,0,1,0,1",
        "0,0,0,0,2,0,4",
        "0,0,0,0,3,0,9",
        "0,0,0,0,4,0,16",

        // science tablets
        "0,0,0,0,0,1,1",
        "0,0,0,0,0,2,4",
        "0,0,0,0,0,3,9",
        "0,0,0,0,0,4,16",

        // science combo
        "0,0,0,1,1,1,10",
        "0,0,0,1,2,3,21",
    })
    @ParameterizedTest
    void getTotalScore(
            int victoryPoints,
            int coins,
            int warPoints,
            int scienceCogwheels,
            int scienceCompasses,
            int scienceTablets,
            int expectedScore
    ) {
        var scoreCard = ScoreCard.builder()
                .victoryPoints(victoryPoints)
                .coins(coins)
                .warPointsScore(warPoints)
                .scienceCogwheels(scienceCogwheels)
                .scienceCompasses(scienceCompasses)
                .scienceTablets(scienceTablets)
                .build();

        var result = scoreCard.getTotalScore();

        assertEquals(expectedScore, result);
    }

    @Test
    void getCoinsScore() {
    }

    @Test
    void getScienceScore() {
    }
}