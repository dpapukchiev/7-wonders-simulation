package dpapukchiev.sevenwonderssimulation.game;

import dpapukchiev.sevenwonderssimulation.cards.Deck;
import dpapukchiev.sevenwonderssimulation.cards.FreeUpgrades;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectExecutionContext;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.reporting.CityStatistics;
import dpapukchiev.sevenwonderssimulation.wonder.CityName;
import dpapukchiev.sevenwonderssimulation.wonder.WonderTemplate;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@Getter
@Log4j2
@RequiredArgsConstructor
public class PlayersFactory {

    private final Deck           deck;
    private final RandomVariable pickACity;
    private final RandomVariable cityDistribution;
    private final CityStatistics cityStatistics;
    private final List<Player>   players = new ArrayList<>();

    public void initialisePlayers(GameOptions options) {
        var wonderTemplate = new WonderTemplate(pickACity);

        players.clear();

        var cities = selectRandomCities(options);
        for (int i = 0; i < options.numberOfPlayers(); i++) {
            var wonderContext = wonderTemplate.build(cities.get(i));
            var providedResources = FreeUpgrades.getProvidedResources(
                    wonderContext.getCityName(),
                    wonderContext.getSide().equals("A")
            );
            var player = Player.builder()
                    .name("Player-%s-%s".formatted(i, wonderContext.getCityName()))
                    .wonderContext(wonderContext)
                    .pickACard(options.playerRandomVariables().get(i))
                    .cityStatistics(cityStatistics)
                    .effectExecutionContext(new EffectExecutionContext(
                            providedResources.getRight(),
                            providedResources.getLeft()
                    ))
                    .build()
                    .initVault(deck);
            players.add(player);
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

        players.forEach(player -> log.info("\nInitialised Player {}", player.report()));
    }

    private List<CityName> selectRandomCities(GameOptions options) {
        var usedCities = new ArrayList<CityName>();

        for (int i = 0; i < options.numberOfPlayers(); i++) {

            var cityName = randomlySelect(CityName.allCities()
                            .stream()
                            .filter(city -> !usedCities.contains(city))
                            .toList(),
                    cityDistribution.getStreamNumber()
            );
            usedCities.add(cityName);
        }

        return usedCities;
    }
}
