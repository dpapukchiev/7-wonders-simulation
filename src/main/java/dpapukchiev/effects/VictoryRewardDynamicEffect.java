package dpapukchiev.effects;

import dpapukchiev.cards.CardType;
import dpapukchiev.player.Player;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;

import static dpapukchiev.effects.VictoryRewardDynamicEffect.CardTypeModifier.COMMERCIAL;
import static dpapukchiev.effects.VictoryRewardDynamicEffect.CardTypeModifier.MANUFACTURED_GOOD;
import static dpapukchiev.effects.VictoryRewardDynamicEffect.CardTypeModifier.RAW_MATERIAL;
import static dpapukchiev.effects.VictoryRewardDynamicEffect.CardTypeModifier.WAR_CARD;
import static dpapukchiev.effects.VictoryRewardDynamicEffect.CardTypeModifier.WAR_LOSS;
import static dpapukchiev.effects.VictoryRewardDynamicEffect.CardTypeModifier.WONDER_STAGE;

@Log4j2
public class VictoryRewardDynamicEffect extends CardEffect {
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
        WAR_CARD,
        WAR_LOSS,
        WONDER_STAGE,
        COMMERCIAL,
        MANUFACTURED_GOOD,
    }

    public VictoryRewardDynamicEffect(
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
        if (List.of(
                WAR_CARD,
                RAW_MATERIAL,
                MANUFACTURED_GOOD,
                COMMERCIAL
        ).contains(cardTypeModifier)) {
            return player.getBuiltCards()
                    .stream()
                    .filter(card -> {
                        CardType cardTypeToCount = switch (cardTypeModifier) {
                            case WAR_CARD -> CardType.MILITARY;
                            case RAW_MATERIAL -> CardType.RAW_MATERIAL;
                            case MANUFACTURED_GOOD -> CardType.MANUFACTURED_GOOD;
                            case COMMERCIAL -> CardType.COMMERCIAL;
                            default -> throw new IllegalStateException("Unexpected value: " + cardTypeModifier);
                        };
                        return card.getType().equals(cardTypeToCount);
                    })
                    .count();
        }
        if (Objects.equals(WONDER_STAGE, cardTypeModifier)) {
            return 0;
        }

        if (Objects.equals(WAR_LOSS, cardTypeModifier)) {
            return (long) Math.abs(player.getWarLossPoints());
        }

        return 0;
    }
}
