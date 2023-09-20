package dpapukchiev.v2.player;

import dpapukchiev.v2.city.CityName;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class WonderContext {
    private CityName cityName;

    public String report() {
        return String.format("W(%s)", cityName);
    }
}
