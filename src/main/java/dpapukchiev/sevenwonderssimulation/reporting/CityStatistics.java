package dpapukchiev.sevenwonderssimulation.reporting;

import dpapukchiev.sevenwonderssimulation.game.GameOptions;
import dpapukchiev.sevenwonderssimulation.game.GamePhase;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.wonder.WonderContext;
import jsl.utilities.statistic.Statistic;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static dpapukchiev.sevenwonderssimulation.game.GamePhase.STATISTICS;

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
        var metricName = getMetricName(wonderContext, name);
        var metric = metrics.getOrDefault(
                metricName,
                new Metric(new Statistic(metricName), wonderContext, player.getName())
        );

//        eventTrackingService.logEvent("%s %s %s-%s".formatted(
//                player.getName(),
//                player.getWonderContext().getName(),
//                name,
//                value
//        ));
        metric.statistic().collect(value);
        metrics.put(metricName, metric);
    }

    public void addWinner(WonderContext wonderContext) {
        winners.putIfAbsent(wonderContext.getName(), 0);
        winners.computeIfPresent(wonderContext.getName(), (k, v) -> v + 1);
    }

    public void log(String event){
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
                .map(s -> "%s: %s (n=%s, std=%s, min=%s, max=%s)".formatted(
                        s.getName(),
                        Math.round(s.getAverage() * 100.0) / 100.0,
                        s.getCount(),
                        Math.round(s.getStandardDeviation() * 100.0) / 100.0,
                        s.getMin(),
                        s.getMax()
                ))
                .forEach(eventTrackingService::logEvent);
//                .forEach(log::info);
    }

    public void reportWinners(int attempts) {
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

    private String getMetricName(WonderContext wonderContext, String statisticName) {
        return switch (sortBy) {
            case CITY -> "%s-%s".formatted(wonderContext.getName(), statisticName);
            case METRIC, METRIC_NAME -> "%s-%s".formatted(statisticName, wonderContext.getName());
        };
    }

}
