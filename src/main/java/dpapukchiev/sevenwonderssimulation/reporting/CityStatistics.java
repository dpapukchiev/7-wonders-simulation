package dpapukchiev.sevenwonderssimulation.reporting;

import dpapukchiev.sevenwonderssimulation.city.CityName;
import jsl.utilities.statistic.Statistic;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class CityStatistics {
    public enum SortBy {
        CITY,
        METRIC
    }

    private final static SortBy                 SORT_BY = SortBy.METRIC;
    // TODO: change to complex key to allow sorting by custom
    private final        Map<String, Statistic> metrics = new HashMap<>();

    private static String getStatisticName(CityName cityName, String statisticName) {
        if (SORT_BY.equals(SortBy.METRIC)) {
            return "%s-%s".formatted(statisticName, cityName.name());
        }
        return "%s-%s".formatted(cityName.name(), statisticName);
    }

    public void trackStatistic(CityName cityName, String statisticName, double value) {
        var metricName = getStatisticName(cityName, statisticName);
        var statistic = metrics.getOrDefault(
                metricName,
                new Statistic(metricName)
        );
        statistic.collect(value);
        metrics.put(metricName, statistic);
    }

    public void reportStatistics() {
        metrics.values().stream()
                .sorted((s1, s2) -> switch (SORT_BY){
                    case CITY, METRIC -> s1.getName().compareTo(s2.getName());
                })
                .map(Statistic::asString)
                .forEach(log::info);
    }

}
