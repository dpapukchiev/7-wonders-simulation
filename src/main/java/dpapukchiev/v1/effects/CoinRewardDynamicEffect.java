package dpapukchiev.v1.effects;

import dpapukchiev.v1.cards.CardType;
import dpapukchiev.v1.player.Player;
import lombok.extern.log4j.Log4j2;

import static dpapukchiev.v1.effects.CoinRewardDynamicEffect.CardTypeModifier.RAW_MATERIAL;

@Log4j2
public class CoinRewardDynamicEffect extends CardEffect {
    private final DirectionModifier directionModifier;
    private final CardTypeModifier  cardTypeModifier;

    public enum DirectionModifier {
        LEFT,
        RIGHT,
        ALL,
        NEIGHBOURS,
        SELF
    }

    public enum CardTypeModifier {
        RAW_MATERIAL,
        MANUFACTURED_GOOD,
    }

    public CoinRewardDynamicEffect(
            double coinReward,
            DirectionModifier directionModifier,
            CardTypeModifier cardTypeModifier
    ) {
        super();
        this.directionModifier = directionModifier;
        this.cardTypeModifier = cardTypeModifier;
        this.effectUsageType = EffectUsageType.DIRECT;
        this.coinReward = coinReward;
        this.maxUsages = 1;
    }

    @Override
    public void applyEffect(Player player) {
        if (!canBeUsed()) {
            return;
        }

        var leftCount = getCount(player.getLeftPlayer());
        var rightCount = getCount(player.getRightPlayer());
        var selfCount = getCount(player);

        if (directionModifier.equals(DirectionModifier.LEFT)) {
            player.rewardCoins(leftCount * coinReward);
        } else if (directionModifier.equals(DirectionModifier.RIGHT)) {
            player.rewardCoins(rightCount * coinReward);
        } else if (directionModifier.equals(DirectionModifier.ALL)) {
            player.rewardCoins((leftCount + rightCount + selfCount) * coinReward);
        } else if (directionModifier.equals(DirectionModifier.SELF)) {
            player.rewardCoins(selfCount * coinReward);
        }

        usedCount++;
        log.info("Player {} rewarded with {} coins", player.getName(), coinReward);
    }

    private long getCount(Player player) {
        return player.getBuiltCards()
                .stream()
                .filter(card -> {
                    CardType cardTypeToCount = cardTypeModifier.equals(RAW_MATERIAL) ?
                            CardType.RAW_MATERIAL : CardType.MANUFACTURED_GOOD;
                    return card.getType().equals(cardTypeToCount);
                })
                .count();
    }
}
