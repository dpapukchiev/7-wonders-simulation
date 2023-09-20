package dpapukchiev.v2.player;

import dpapukchiev.v2.cards.Card;
import dpapukchiev.v2.cards.CardType;
import dpapukchiev.v2.effects.EffectMultiplierType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dpapukchiev.v2.cards.CardType.MANUFACTURED_GOOD;
import static dpapukchiev.v2.cards.CardType.RAW_MATERIAL;
import static java.util.stream.Collectors.groupingBy;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vault {
    @Builder.Default
    private List<WarPoint> warPoints     = new ArrayList<>();
    @Builder.Default
    private double         victoryPoints = 0;
    @Builder.Default
    private double         shields       = 0;
    @Builder.Default
    private double         coins         = 3;
    @Builder.Default
    private List<Card>     builtCards    = new ArrayList<>();

    public void addWarPoint(WarPoint warPoint) {
        warPoints.add(warPoint);
    }

    public double getWarPointsScore() {
        return warPoints.stream().mapToInt(WarPoint::getValue).sum();
    }

    public void addVictoryPoints(double victoryPoints) {
        this.victoryPoints += victoryPoints;
    }

    public void addShields(double shields) {
        this.shields += shields;
    }

    public void addCoins(double coins) {
        this.coins += coins;
    }

    public void removeCoins(double coins) {
        this.coins -= coins;
    }

    public void addBuiltCard(Card card) {
        builtCards.add(card);
    }

    public long countMultiplier(EffectMultiplierType effectMultiplierType) {
        return countCards(effectMultiplierType);
    }

    private long countCards(EffectMultiplierType effectMultiplierType) {
        return switch (effectMultiplierType) {
            case RAW_MATERIAL_CARD -> getCardCount(RAW_MATERIAL);
            case MANUFACTURED_GOOD_CARD -> getCardCount(MANUFACTURED_GOOD);
            case MILITARY_CARD -> getCardCount(CardType.MILITARY);
            case SCIENCE_CARD -> getCardCount(CardType.SCIENCE);
            case COMMERCIAL_CARD -> getCardCount(CardType.COMMERCIAL);
            case CIVIL_CARD -> getCardCount(CardType.CIVIL);
            case WAR_LOSS -> getWarPointOfType(WarPoint.MINUS_ONE);
            case WAR_WIN_1 -> getWarPointOfType(WarPoint.ONE);
            case WAR_WIN_3 -> getWarPointOfType(WarPoint.THREE);
            case WAR_WIN_5 -> getWarPointOfType(WarPoint.FIVE);
            case WAR_WIN -> getWarPointOfType(null);
            case WONDER_STAGE -> {
                yield 0;
            } // TODO: implement
        };
    }

    public Set<String> getBuiltCardNames() {
        return getBuiltCards().stream().map(Card::getName).collect(Collectors.toSet());
    }

    private long getWarPointOfType(WarPoint warPointType) {
        return getWarPoints()
                .stream()
                .filter(warPoint -> (warPointType == null && warPoint.getValue() > 0) || warPoint.equals(warPointType))
                .count();
    }

    private long getCardCount(CardType cardType) {
        var results = getBuiltCards().stream().collect(groupingBy(Card::getType))
                .get(cardType);
        if (results.isEmpty()) {
            return 0;
        }
        return results.size();
    }
}
