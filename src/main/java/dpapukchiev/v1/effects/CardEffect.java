package dpapukchiev.v1.effects;

import dpapukchiev.v1.cards.Card;
import dpapukchiev.v1.cards.ManufacturedGood;
import dpapukchiev.v1.cards.RawMaterial;
import dpapukchiev.v1.cards.ScienceSymbol;
import dpapukchiev.v1.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
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
    protected PreferentialTrading    preferentialTrading;

    public boolean canBeUsed() {
        return (maxUsages == null) || usedCount < maxUsages;
    }

    public String report() {
        var report = new ArrayList<String>();
        if (coinReward > 0) {
            report.add("$: " + coinReward);
        }
        if (pointsAward > 0) {
            report.add("V: " + pointsAward);
        }
        if (shieldsAward > 0) {
            report.add("X: " + shieldsAward);
        }
        if (!providedRawMaterials.isEmpty()) {
            report.add("RM: " + providedRawMaterials.stream()
                    .map(rm -> rm.name().substring(0, 1))
                    .collect(Collectors.joining("-")));
        }
        if (!providedManufacturedGood.isEmpty()) {
            report.add("MG: " + providedManufacturedGood.stream()
                    .map(rm -> rm.name().substring(0, 1))
                    .collect(Collectors.joining("-")));
        }
        if (!providedScienceSymbols.isEmpty()) {
            report.add("S: " + providedScienceSymbols.stream()
                    .map(rm -> rm.name().substring(0, 2))
                    .collect(Collectors.joining("-")));
        }
        if (!freeCards.isEmpty()) {
            report.add("FC: " + freeCards.stream()
                    .map(Card::getName)
                    .collect(Collectors.joining("-")));
        }
        if (preferentialTrading != null) {
            report.add("PT: %s".formatted(
                    preferentialTrading.report()
            ));
        }
        return "E(%s)".formatted(String.join(" ", report));
    }

    public void applyEffect(Player player) {
        if (!canBeUsed()) {
            return;
        }

        player.rewardCoins(coinReward);

        usedCount++;
        log.info("Player {} rewarded with {} coins", player.getName(), coinReward);
    }
}
