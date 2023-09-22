package dpapukchiev.sevenwonderssimulation.player;

import dpapukchiev.sevenwonderssimulation.city.CityName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WonderContext {
    private CityName cityName;

    public String report() {
        return String.format("W(%s)", cityName);
    }
}
