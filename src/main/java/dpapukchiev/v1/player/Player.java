package dpapukchiev.v1.player;

import dpapukchiev.v1.cards.Card;
import dpapukchiev.v1.cards.HandOfCards;
import dpapukchiev.v1.cards.ManufacturedGood;
import dpapukchiev.v1.cards.RawMaterial;
import dpapukchiev.v1.city.CityName;
import dpapukchiev.v1.cost.CostReport;
import dpapukchiev.v1.effects.CardEffect;
import dpapukchiev.v1.effects.EffectUsageType;
import dpapukchiev.v1.effects.PreferentialTrading;
import dpapukchiev.v1.effects.v2.EffectExecutionContext;
import dpapukchiev.v1.effects.v2.ResourceContext;
import dpapukchiev.v1.game.TurnContext;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dpapukchiev.v1.effects.PreferentialTrading.PreferentialTradingType.LEFT;
import static dpapukchiev.v1.effects.PreferentialTrading.PreferentialTradingType.RIGHT;
import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@Log4j2
@Setter
@Getter
@Builder
@ToString(exclude = {"leftPlayer", "rightPlayer"})
public class Player {
    @Builder.Default
    private double                 coins                  = 3;
    private String                 name;
    private CityName               city;
    private RandomVariable         pickACard;
    @Builder.Default
    private EffectExecutionContext effectExecutionContext = new EffectExecutionContext();
    @Builder.Default
    private double                 warWinPoints           = 0;
    @Builder.Default
    private double                 warLossPoints          = 0;
    @Builder.Default
    private List<Card>             builtCards             = new ArrayList<>();
    @Builder.Default
    private List<Card>             discardedCards         = new ArrayList<>();
    @Builder.Default
    private Set<String>            builtCardNames         = new HashSet<>();
    private Player                 leftPlayer;
    private Player                 rightPlayer;

    public ResourceContext getResourceContext() {
        return new ResourceContext(this);
    }

    public void rewardCoins(double coins) {
        this.coins += coins;
    }

    public void removeCoins(double coins) {
        this.coins -= coins;
    }

    public double getRawMaterialCount(RawMaterial rawMaterial) {
        var validEffects = builtCards.stream()
                .map(Card::getEffect)
                .filter(effect -> !effect.isWildcardRawMaterial());
        return countMaterial(rawMaterial, validEffects);
    }

    public double getManufacturedGoodCount(ManufacturedGood manufacturedGood) {
        var validEffects = builtCards.stream()
                .map(Card::getEffect)
                .filter(effect -> !effect.isWildcardManufacturedGood());
        return countMaterial(manufacturedGood, validEffects);
    }

    public double getManufacturedGoodCountWildcard(ManufacturedGood manufacturedGood) {
        var validEffects = builtCards.stream()
                .map(Card::getEffect)
                .filter(CardEffect::isWildcardManufacturedGood);
        return countMaterial(manufacturedGood, validEffects);
    }

    public double getRawMaterialCountWildcard(RawMaterial rawMaterial) {
        var validEffects = builtCards.stream()
                .map(Card::getEffect)
                .filter(CardEffect::isWildcardRawMaterial);
        return countMaterial(rawMaterial, validEffects);
    }

    public double getShieldCount() {
        return builtCards.stream()
                .map(Card::getEffect)
                .mapToDouble(CardEffect::getShieldsAward)
                .filter(Objects::nonNull)
                .sum();
    }

    public void executeEndOfAge(int age) {
        builtCards.stream()
                .filter(c -> c.getAge() == age)
                .filter(c -> !c.getEffect().canBeUsed())
                .filter(c -> c.getEffect().getEffectUsageType().equals(EffectUsageType.END_OF_AGE))
                .forEach(c -> c.getEffect().applyEffect(this));

        executeWar(age);
    }

    public void executeTurn(TurnContext turnContext) {
        var startingCoins = turnContext.getPlayer().getCoins();
        var handOfCards = turnContext.getHandOfCards();

        var cardsToPickFrom = handOfCards.getBuildableCards(turnContext);

        if (cardsToPickFrom.isEmpty()) {
            var cardToDiscard = randomlySelect(handOfCards.getCards(), pickACard.getStreamNumber());
            handOfCards.getCards().remove(cardToDiscard);
            turnContext.getPlayer().rewardCoins(3);

            discardedCards.add(cardToDiscard);
            handOfCards.discard(cardToDiscard);
            return;
        }

        var card = randomlySelect(cardsToPickFrom, pickACard.getStreamNumber());

        var costReport = processSelectedCard(turnContext, card, handOfCards);

        log.info(
                """
                        Step: {}
                        Executed turn => age: {}, turn: {}
                        Starting coins: {}
                        From hand: {}
                        Selected card: {}
                        Cost: {}
                        Player info:
                        {}
                        """,
                turnContext.getSimulationStep(),
                turnContext.getAge(),
                turnContext.getTurnCountAge(),
                startingCoins,
                handOfCards.getUuid(),
                card.report(),
                costReport,
                report(turnContext.getAge())
        );
    }

    private Map<String, Double> getResourcesMap() {
        var result = new HashMap<String, Double>();
        List.of(RawMaterial.values())
                .forEach(rm -> result.put(rm.name(), getRawMaterialCount(rm) + getRawMaterialCountWildcard(rm)));

        List.of(ManufacturedGood.values())
                .forEach(mg -> result.put(mg.name(), getManufacturedGoodCount(mg) + getManufacturedGoodCountWildcard(mg)));
        return result;
    }

    private CostReport processSelectedCard(TurnContext turnContext, Card card, HandOfCards handOfCards) {
        var costReport = card.getCost().generateCostReport(turnContext);
        boolean isFreeUpgrade = builtCards.stream()
                .anyMatch(c -> c.getFreeUpgrades().contains(card.getName()));
        if (!isFreeUpgrade) {
            card.getCost().applyCost(turnContext, costReport);
        }

        if (card.getEffect().getEffectUsageType().equals(EffectUsageType.DIRECT)) {
            card.getEffect().applyEffect(turnContext.getPlayer());
        }

        builtCardNames.add(card.getName());
        builtCards.add(card);
        handOfCards.getCards().remove(card);
        return costReport;
    }

    public void executeWar(int age) {
        var pointsForAge = age != 1 ? (age != 2 ? 5 : 3) : 1;
        var myShields = getShieldCount();
        var leftShields = getLeftPlayer().getShieldCount();
        var rightShields = getRightPlayer().getShieldCount();

        if (myShields > leftShields) {
            warWinPoints += pointsForAge;
        } else if (myShields < leftShields) {
            warLossPoints--;
        }

        if (myShields > rightShields) {
            warWinPoints += pointsForAge;
        } else if (myShields < rightShields) {
            warLossPoints--;
        }
    }

    public String report(int age) {
        var cards = builtCards.stream()
                .map(Card::report)
                .collect(Collectors.joining("\n"));
        return String.format("""
                                        
                        %s (age %d)
                        coins: %.1f
                        war loss: %.1f
                        war win: %.1f
                        shields: %.1f
                        resources: %s
                        built cards (%d):
                        %s
                        """,
                getName(),
                age,
                getCoins(),
                getWarLossPoints(),
                getWarWinPoints(),
                getShieldCount(),
                getResourcesMap(),
                builtCards.size(),
                cards
        );
    }

    public double getTradingPriceLeft(RawMaterial rawMaterial) {
        return getCardsWithPreferentialTrading(LEFT)
                .anyMatch(c -> c.getEffect().getPreferentialTrading().rawMaterials().contains(rawMaterial)) ?
                1 : 2;
    }

    public double getTradingPriceRight(RawMaterial rawMaterial) {
        return getCardsWithPreferentialTrading(RIGHT)
                .anyMatch(c -> c.getEffect().getPreferentialTrading().rawMaterials().contains(rawMaterial)) ?
                1 : 2;
    }

    public double getTradingPriceLeft(ManufacturedGood manufacturedGood) {
        return getCardsWithPreferentialTrading(LEFT)
                .anyMatch(c -> c.getEffect().getPreferentialTrading().manufacturedGoods().contains(manufacturedGood)) ?
                1 : 2;
    }

    public double getTradingPriceRight(ManufacturedGood manufacturedGood) {
        return getCardsWithPreferentialTrading(RIGHT)
                .anyMatch(c -> c.getEffect().getPreferentialTrading().manufacturedGoods().contains(manufacturedGood)) ?
                1 : 2;
    }

    private Stream<Card> getCardsWithPreferentialTrading(PreferentialTrading.PreferentialTradingType tradingType) {
        return builtCards.stream()
                .filter(c -> c.getEffect().getPreferentialTrading() != null)
                .filter(c -> List.of(
                        PreferentialTrading.PreferentialTradingType.BOTH,
                        tradingType
                ).contains(c.getEffect().getPreferentialTrading().type()));
    }

    private static int countMaterial(RawMaterial rawMaterial, Stream<CardEffect> validEffects) {
        return validEffects
                .filter(effect -> effect.getProvidedRawMaterials().contains(rawMaterial))
                .map(effect -> effect.getProvidedRawMaterials()
                        .stream()
                        .filter(m -> m.equals(rawMaterial))
                        .count()
                )
                .mapToInt(Long::intValue)
                .sum();
    }

    private static int countMaterial(ManufacturedGood manufacturedGood, Stream<CardEffect> validEffects) {
        return validEffects
                .filter(effect -> effect.getProvidedManufacturedGood().contains(manufacturedGood))
                .map(effect -> effect.getProvidedManufacturedGood()
                        .stream()
                        .filter(m -> m.equals(manufacturedGood))
                        .count()
                )
                .mapToInt(Long::intValue)
                .sum();
    }

}