package dpapukchiev.sevenwonderssimulation.reporting;

import dpapukchiev.sevenwonderssimulation.game.GameOptions;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.player.strategy.Strategy;
import dpapukchiev.sevenwonderssimulation.wonder.WonderContext;
import jsl.utilities.statistic.Statistic;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static dpapukchiev.sevenwonderssimulation.game.GamePhase.STATISTICS;
import static dpapukchiev.sevenwonderssimulation.game.GamePhase.WINNERS;

@Log4j2
@Getter
public class CityStatistics {
    private final SortBy               sortBy;
    private final Map<String, Integer> winners = new HashMap<>();
    private       EventTrackingService eventTrackingService;

    public CityStatistics(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    public void refreshEventTrackingService(GameOptions gameOptions, double replicationNumber) {
        eventTrackingService = new EventTrackingService(
                "%s-%s".formatted(gameOptions.startTime().toString(), gameOptions.runId()),
                replicationNumber,
                gameOptions.logEveryNGames(),
                gameOptions.simulation()
        );
    }

    public enum SortBy {
        CITY,
        METRIC,
        METRIC_NAME
    }

    public record Metric(
            Statistic statistic,
            WonderContext wonderContext,
            String playerName
    ) {
    }

    private final Map<String, Metric> metrics = new HashMap<>();

    public void collectMetric(String name, double value, Player player) {
        var wonderContext = player.getWonderContext();
        var metricName = getMetricName(wonderContext, name, player.getStrategy());
        var metric = metrics.getOrDefault(
                metricName,
                new Metric(new Statistic(metricName), wonderContext, player.getName())
        );

        metric.statistic().collect(value);
        metrics.put(metricName, metric);
    }

    public void addWinner(Player player) {
        var winnerName = player.getWonderContext().getName() + "-" + player.getStrategy().getName().name();
        winners.putIfAbsent(winnerName, 0);
        winners.computeIfPresent(winnerName, (k, v) -> v + 1);
        player.collectMetric("winner-times", 1);
    }

    public void log(String event) {
        eventTrackingService.logEvent(event);
    }

    public void reportStatistics(SortBy sortBy) {
        eventTrackingService.transitionPhase(STATISTICS);
        eventTrackingService.logEvent("\nSTATISTICS BY %s".formatted(sortBy));

        metrics.values().stream()
                .sorted((s1, s2) -> switch (sortBy) {
                    case CITY -> s1.wonderContext().getCityName().name()
                            .compareTo(s2.wonderContext().getCityName().name());
                    case METRIC -> s1.statistic().compareTo(s2.statistic());
                    case METRIC_NAME -> s1.statistic().getName().compareTo(s2.statistic().getName());
                })
                .map(Metric::statistic)
                .forEach(eventTrackingService::logStatistic);
//                .forEach(log::info);
    }

    public void reportWinners(int attempts) {
        eventTrackingService.transitionPhase(WINNERS);

        eventTrackingService.logEvent("\nFINAL RANKING BY WINS");
        winners.entrySet().stream()
                // sort by number of wins desc
                .sorted((e1, e2) -> Comparator.comparingInt(
                        Map.Entry<String, Integer>::getValue
                ).reversed().compare(e1, e2))
                .forEach(e -> eventTrackingService.logEvent("%s won %s/%s games".formatted(e.getKey(), e.getValue(), attempts)));

        eventTrackingService.logEvent("\nFINAL RANKING BY AVERAGE SCORE");
        var finalRanking = metrics.values().stream()
                .filter(m -> m.statistic().getName().contains("score-total"))
                .sorted(Comparator.comparing(Metric::statistic)
                        .reversed())
                .map(Metric::statistic)
                .toList();

        for (int i = 1; i <= finalRanking.size(); i++) {
            var statistic = finalRanking.get(i - 1);
            eventTrackingService.logEvent("Place: %s %s avg=%s (n=%s min=%s max=%s)"
                    .formatted(i,
                            statistic.getName(),
                            Math.round(statistic.getAverage() * 100.0) / 100.0,
                            statistic.getCount(),
                            statistic.getMin(),
                            statistic.getMax()
                    ));
        }
    }

    private String getMetricName(WonderContext wonderContext, String statisticName, Strategy strategy) {
        return switch (sortBy) {
            case CITY -> "%s-%s-%s".formatted(wonderContext.getName(), statisticName, strategy.getName().name());
            case METRIC, METRIC_NAME ->
                    "%s-%s-%s".formatted(statisticName, wonderContext.getName(), strategy.getName().name());
        };
    }

}
