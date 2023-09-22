package dpapukchiev.sevenwonderssimulation.reporting;

import dpapukchiev.sevenwonderssimulation.city.CityName;
import dpapukchiev.sevenwonderssimulation.player.Player;
import jsl.utilities.statistic.Statistic;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class CityStatistics {
    private final SortBy sortBy;

    public CityStatistics(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    public enum SortBy {
        CITY,
        METRIC,
        METRIC_NAME
    }

    public record Metric(
            Statistic statistic,
            CityName cityName,
            String playerName
    ) {
    }

    private final Map<String, Metric> metrics = new HashMap<>();

    private String getStatisticName(CityName cityName, String statisticName) {
        return switch (sortBy) {
            case CITY -> "%s-%s".formatted(cityName.name(), statisticName);
            case METRIC, METRIC_NAME -> "%s-%s".formatted(statisticName, cityName.name());
        };
    }

    public void collectMetric(CityName cityName, String statisticName, double value, Player player) {
        var metricName = getStatisticName(cityName, statisticName);
        var metric = metrics.getOrDefault(
                metricName,
                new Metric(new Statistic(metricName), cityName, player.getName())
        );

        metric.statistic().collect(value);
        metrics.put(metricName, metric);
    }

    public void reportStatistics(SortBy sortBy) {
        log.info("\nSTATISTICS BY %s".formatted(sortBy));
        metrics.values().stream()
                .sorted((s1, s2) -> switch (sortBy) {
                    case CITY -> s1.cityName().name().compareTo(s2.cityName().name());
                    case METRIC -> s1.statistic().compareTo(s2.statistic());
                    case METRIC_NAME -> s1.statistic().getName().compareTo(s2.statistic().getName());
                })
                .map(Metric::statistic)
                .map(s -> "%s: %s (n=%s, std=%s) ".formatted(
                        s.getName(),
                        Math.round(s.getAverage() * 100.0) / 100.0,
                        s.getCount(),
                        Math.round(s.getStandardDeviation() * 100.0) / 100.0
                ))
                .forEach(log::info);
    }

}
