package dpapukchiev.sevenwonderssimulation.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GamePhase {
    INITIALIZE_PLAYERS("Initialize players"),
    PLAYER_TURN_AGE_1_TURN_1("Players turn age 1 turn 1"),
    END_OF_TURN_AGE_1_TURN_1("End of age 1 turn 1"),
    PLAYER_TURN_AGE_1_TURN_2("Players turn age 1 turn 2"),
    END_OF_TURN_AGE_1_TURN_2("End of age 1 turn 2"),
    PLAYER_TURN_AGE_1_TURN_3("Players turn age 1 turn 3"),
    END_OF_TURN_AGE_1_TURN_3("End of age 1 turn 3"),
    PLAYER_TURN_AGE_1_TURN_4("Players turn age 1 turn 4"),
    END_OF_TURN_AGE_1_TURN_4("End of age 1 turn 4"),
    PLAYER_TURN_AGE_1_TURN_5("Players turn age 1 turn 5"),
    END_OF_TURN_AGE_1_TURN_5("End of age 1 turn 5"),
    PLAYER_TURN_AGE_1_TURN_6("Players turn age 1 turn 6"),
    END_OF_TURN_AGE_1_TURN_6("End of age 1 turn 6"),
    END_OF_AGE_1("End of age 1"),
    PLAYER_TURN_AGE_2_TURN_1("Players turn age 2 turn 1"),
    END_OF_TURN_AGE_2_TURN_1("End of age 2 turn 1"),
    PLAYER_TURN_AGE_2_TURN_2("Players turn age 2 turn 2"),
    END_OF_TURN_AGE_2_TURN_2("End of age 2 turn 2"),
    PLAYER_TURN_AGE_2_TURN_3("Players turn age 2 turn 3"),
    END_OF_TURN_AGE_2_TURN_3("End of age 2 turn 3"),
    PLAYER_TURN_AGE_2_TURN_4("Players turn age 2 turn 4"),
    END_OF_TURN_AGE_2_TURN_4("End of age 2 turn 4"),
    PLAYER_TURN_AGE_2_TURN_5("Players turn age 2 turn 5"),
    END_OF_TURN_AGE_2_TURN_5("End of age 2 turn 5"),
    PLAYER_TURN_AGE_2_TURN_6("Players turn age 2 turn 6"),
    END_OF_TURN_AGE_2_TURN_6("End of age 2 turn 6"),
    END_OF_AGE_2("End of age 2"),
    PLAYER_TURN_AGE_3_TURN_1("Players turn age 3 turn 1"),
    END_OF_TURN_AGE_3_TURN_1("End of age 3 turn 1"),
    PLAYER_TURN_AGE_3_TURN_2("Players turn age 3 turn 2"),
    END_OF_TURN_AGE_3_TURN_2("End of age 3 turn 2"),
    PLAYER_TURN_AGE_3_TURN_3("Players turn age 3 turn 3"),
    END_OF_TURN_AGE_3_TURN_3("End of age 3 turn 3"),
    PLAYER_TURN_AGE_3_TURN_4("Players turn age 3 turn 4"),
    END_OF_TURN_AGE_3_TURN_4("End of age 3 turn 4"),
    PLAYER_TURN_AGE_3_TURN_5("Players turn age 3 turn 5"),
    END_OF_TURN_AGE_3_TURN_5("End of age 3 turn 5"),
    PLAYER_TURN_AGE_3_TURN_6("Players turn age 3 turn 6"),
    END_OF_TURN_AGE_3_TURN_6("End of age 3 turn 6"),
    END_OF_AGE_3("End of age 3"),
    END_OF_GAME("End of Game"),
    SCORING("Scoring"),
    STATISTICS("Statistics"),
    WINNERS("Winners");

    public static GamePhase playerTurn(int age, int turn) {
        return valueOf("PLAYER_TURN_AGE_%s_TURN_%s".formatted(age, turn));
    }

    public static GamePhase endOfTurn(int age, int turn) {
        return valueOf("END_OF_TURN_AGE_%s_TURN_%s".formatted(age, turn));
    }

    public static GamePhase endOfAge(int age) {
        return valueOf("END_OF_AGE_%s".formatted(age));
    }

    private final String displayName;

}
