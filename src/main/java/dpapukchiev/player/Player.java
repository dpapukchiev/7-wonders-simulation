package dpapukchiev.player;

import dpapukchiev.cards.Card;
import dpapukchiev.cards.CardType;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.city.CityName;
import dpapukchiev.effects.CardEffect;
import dpapukchiev.game.TurnContext;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static jsl.utilities.random.rvariable.JSLRandom.randomlySelect;

@Log4j2
@Setter
@Getter
@Builder
@ToString(exclude = {"leftPlayer", "rightPlayer"})
public class Player {
    @Builder.Default
    private double coins = 3;
    private String name;
    private CityName city;
    private RandomVariable pickACard;
    @Builder.Default
    private double warWinPoints = 0;
    @Builder.Default
    private double warLossPoints = 0;
    @Builder.Default
    private List<Card> builtCards = new ArrayList<>();
    @Builder.Default
    private Set<String> builtCardNames = new HashSet<>();
    private Player leftPlayer;
    private Player rightPlayer;

    public void rewardCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public Map<CardType, List<Card>> getBuiltCardsByType() {
        return builtCards.stream().collect(groupingBy(Card::getType));
    }

    public double getRawMaterialCount(RawMaterial rawMaterial) {
        return builtCards.stream()
                .map(Card::getEffect)
                .filter(effect -> effect.getProvidedRawMaterials().contains(rawMaterial))
                .map(effect -> effect.getProvidedRawMaterials()
                        .stream()
                        .filter(m -> m.equals(rawMaterial))
                        .count()
                )
                .mapToInt(Long::intValue)
                .sum();
    }

    public double getShieldCount() {
        return builtCards.stream()
                .map(Card::getEffect)
                .mapToDouble(CardEffect::getShieldsAward)
                .filter(Objects::nonNull)
                .sum();
    }

    public void executeTurn(TurnContext turnContext) {
        var handOfCards = turnContext.getHandOfCards();

        // buy resources
        // free upgrades
        var cardsToPickFrom = handOfCards.getCards().stream()
                .filter(c -> c.getCost().canBuild(turnContext))
                .filter(c -> !builtCardNames.contains(c.getName()))
                .toList();

        if (cardsToPickFrom.isEmpty()) {
            var cardToDiscard = randomlySelect(handOfCards.getCards(), pickACard.getStreamNumber());
            handOfCards.getCards().remove(cardToDiscard);
            turnContext.getPlayer().rewardCoins(3);
            return;
        }

        var card = randomlySelect(cardsToPickFrom, pickACard.getStreamNumber());
        card.getCost().applyCost(turnContext);

        builtCardNames.add(card.getName());
        builtCards.add(card);
        handOfCards.getCards().remove(card);

        log.info(
                """
                        Step: {}
                        Executed turn => age: {}, turn: {}
                        From hand: {}
                        Selected card: {}
                        Player info:
                        {}
                        """,
                turnContext.getSimulationStep(),
                turnContext.getAge(),
                turnContext.getTurnCountAge(),
                handOfCards.getUuid(),
                card.report(),
                report(turnContext.getAge())
        );
    }

    public void executeWar(int age) {
        var pointsForAge = age != 1 ? (age != 2 ? 5 : 3) : 1;
        var myShields = getShieldCount();
        var leftShields = getLeftPlayer().getShieldCount();
        var rightShields = getLeftPlayer().getShieldCount();

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

    public String report(int age){
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

}