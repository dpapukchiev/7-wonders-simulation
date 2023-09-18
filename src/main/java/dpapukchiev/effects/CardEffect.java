package dpapukchiev.effects;

import dpapukchiev.cards.Card;
import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.cards.ScienceSymbol;
import dpapukchiev.player.Player;
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
    protected Integer                maxUsages;
    @Builder.Default
    protected Integer                usedCount                = 0;
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
    protected boolean                wildcardScienceSymbol;
    @Builder.Default
    protected List<ScienceSymbol>    providedScienceSymbols   = new ArrayList<>();
    @Builder.Default
    protected List<Card>             freeCards                = new ArrayList<>();

    public boolean canBeUsed() {
        return (maxUsages == null) || usedCount < maxUsages;
    }

    public String report() {
        return "E($%.1f V%.1f X%.1f RM:%s MG:%s S:%s)"
                .formatted(
                        coinReward,
                        pointsAward,
                        shieldsAward,
                        providedRawMaterials.stream()
                                .map(rm -> rm.name().substring(0, 1))
                                .collect(Collectors.joining()),
                        providedManufacturedGood.stream()
                                .map(rm -> rm.name().substring(0, 1))
                                .collect(Collectors.joining()),
                        providedScienceSymbols.stream()
                                .map(rm -> rm.name().substring(0, 1))
                                .collect(Collectors.joining())
                );
    }

    public void applyEffect(Player player) {
        if (!canBeUsed()) {
            return;
        }

        player.rewardCoins(coinReward);

        usedCount++;
    }
}
