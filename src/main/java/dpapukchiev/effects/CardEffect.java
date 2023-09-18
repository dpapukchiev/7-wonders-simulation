package dpapukchiev.effects;

import dpapukchiev.cards.Card;
import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardEffect {
    protected EffectUsageType        effectUsageType;
    protected int                    maxUsages;
    protected int                    usedCount;
    @Builder.Default
    protected Double                 coinReward               = 0d;
    @Builder.Default
    protected Double                 pointsAward              = 0d;
    @Builder.Default
    protected Double                 shieldsAward             = 0d;
    @Builder.Default
    protected List<ManufacturedGood> providedManufacturedGood = new ArrayList<>();
    protected boolean                wildcardManufacturedGood;
    protected boolean                wildcardRawMaterial;
    @Builder.Default
    protected List<RawMaterial>      providedRawMaterials     = new ArrayList<>();
    @Builder.Default
    protected List<Card>             freeCards                = new ArrayList<>();

    public String report() {
        return "E($%.1f V%.1f X%.1f R:%s G:%s)"
                .formatted(
                        coinReward,
                        pointsAward,
                        shieldsAward,
                        providedRawMaterials.stream()
                                .map(rm -> rm.name().substring(0, 1))
                                .collect(Collectors.joining()),
                        providedManufacturedGood.stream()
                                .map(rm -> rm.name().substring(0, 1))
                                .collect(Collectors.joining())
                );
    }
}
