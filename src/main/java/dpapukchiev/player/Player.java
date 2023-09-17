package dpapukchiev.player;

import dpapukchiev.cards.Card;
import dpapukchiev.cards.CardType;
import dpapukchiev.cards.RawMaterial;
import dpapukchiev.city.CityName;
import dpapukchiev.effects.EffectUsageType;
import dpapukchiev.game.TurnContext;
import jsl.modeling.elements.variable.RandomVariable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
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
    private List<Card> builtCards = new ArrayList<>();
    @Builder.Default
    private Set<String> builtCardNames = new HashSet<>();
    private Player leftPlayer;
    private Player rightPlayer;

    public void rewardCoins(int coins){
        this.coins += coins;
    }

    public Map<CardType, List<Card>> getBuiltCardsByType(){
        return builtCards.stream().collect(groupingBy(Card::getType));
    }

    public long getRawMaterialCount(RawMaterial rawMaterial){
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

    public void executeTurn(TurnContext turnContext) {
        var handOfCards = turnContext.getHandOfCards();

        // buy resources
        // free upgrades
        var cardsToPickFrom = handOfCards.getCards().stream()
                .filter(c -> c.getCost().canBuild(turnContext))
                .filter(c -> !builtCardNames.contains(c.getName()))
                .toList();

        if(cardsToPickFrom.isEmpty()){
            var cardToDiscard = randomlySelect(handOfCards.getCards(), pickACard.getStreamNumber());
            handOfCards.getCards().remove(cardToDiscard);
            turnContext.getPlayer().rewardCoins(3);
            return;
        }

        var card = randomlySelect(cardsToPickFrom, pickACard.getStreamNumber());

        builtCardNames.add(card.getName());
        builtCards.add(card);
        handOfCards.getCards().remove(card);

        log.info(
                """
                                                
                        {}
                        Executed turn age: {}, turn: {}
                        player: {},
                        hand: {} ({}),
                        selected: {}
                        built: {}
                        """,
                turnContext.getSimulationStep(),
                turnContext.getAge(),
                turnContext.getTurnCountAge(),
                turnContext.getPlayer().getName(),
                handOfCards.getUuid(),
                handOfCards.getCards().size(),
                card.getName(),
                builtCards.size()
        );
    }
}