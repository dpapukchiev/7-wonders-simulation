package dpapukchiev.player;

import dpapukchiev.cards.Card;
import dpapukchiev.cards.HandOfCards;
import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.city.CityName;
import dpapukchiev.cost.CostReport;
import dpapukchiev.effects.CardEffect;
import dpapukchiev.effects.EffectUsageType;
import dpapukchiev.game.TurnContext;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@Log4j2
@Setter
@Getter
@Builder
@ToString(exclude = {"leftPlayer", "rightPlayer"})
public class Player {
    @Builder.Default
    private double         coins          = 3;
    private String         name;
    private CityName       city;
    private RandomVariable pickACard;
    @Builder.Default
    private double         warWinPoints   = 0;
    @Builder.Default
    private double         warLossPoints  = 0;
    @Builder.Default
    private List<Card>     builtCards     = new ArrayList<>();
    @Builder.Default
    private List<Card>     discardedCards = new ArrayList<>();
    @Builder.Default
    private Set<String>    builtCardNames = new HashSet<>();
    private Player         leftPlayer;
    private Player         rightPlayer;

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

        // buy resources
        // free upgrades
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
                        built cards (%d):
                        %s
                        """,
                getName(),
                age,
                getCoins(),
                getWarLossPoints(),
                getWarWinPoints(),
                getShieldCount(),
                builtCards.size(),
                cards
        );
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