package dpapukchiev.v2.game;

import dpapukchiev.v2.city.CityName;
import dpapukchiev.v2.player.Player;
import dpapukchiev.v2.player.WonderContext;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@Getter
@Log4j2
@RequiredArgsConstructor
public class PlayersFactory {

    private final RandomVariable cityDistribution;
    private final List<Player>   players = new ArrayList<>();

    public void initialisePlayers(GameOptions options) {
        players.clear();

        var cities = selectRandomCities(options);
        for (int i = 0; i < options.numberOfPlayers(); i++) {
            players.add(Player.builder()
                    .name("Player-" + i)
                    .wonderContext(WonderContext.builder()
                            .cityName(cities.get(i))
                            .build())
                    .pickACard(options.playerRandomVariables().get(i))
                    .build());
        }

        for (int i = 0; i < players.size(); i++) {
            var player = players.get(i);
            var previousPlayer = i == 0 ? players.size() - 1 : i - 1;
            var nextPlayer = i == (players.size() - 1) ? 0 : i + 1;

            player.setLeftPlayer(players.get(previousPlayer));
            player.setRightPlayer(players.get(nextPlayer));
            log.info("{}=> left: {}, right: {}",
                    player.getName(),
                    player.getLeftPlayer().getName(),
                    player.getRightPlayer().getName()
            );
        }
        log.info("Initialised Players {}", players);
    }

    private List<CityName> selectRandomCities(GameOptions options) {
        var usedCities = new ArrayList<CityName>();

        for (int i = 0; i < options.numberOfPlayers(); i++) {

            var cityName = randomlySelect(Stream.of(CityName.values())
                            .filter(city -> !usedCities.contains(city))
                            .toList(),
                    cityDistribution.getStreamNumber()
            );
            usedCities.add(cityName);
        }

        return usedCities;
    }
}
