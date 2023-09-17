package dpapukchiev.cards;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class HandOfCards {
    @ToString.Include
    private UUID uuid;
    private List<Card> cards;
}
