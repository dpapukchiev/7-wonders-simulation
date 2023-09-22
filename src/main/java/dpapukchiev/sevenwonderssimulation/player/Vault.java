package dpapukchiev.sevenwonderssimulation.player;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardName;
import dpapukchiev.sevenwonderssimulation.cards.CardType;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vault {
    @Builder.Default
    private List<WarPoint> warPoints      = new ArrayList<>();
    @Builder.Default
    private double         victoryPoints  = 0;
    @Builder.Default
    private double         shields        = 0;
    @Builder.Default
    private double         coins          = 3;
    @Builder.Default
    private List<Card>     builtCards     = new ArrayList<>();
    @Builder.Default
    private List<Card>     discardedCards = new ArrayList<>();

    public List<String> getBuiltCardNames() {
        return getBuiltCards()
                .stream()
                .map(Card::getName)
                .map(CardName::name)
                .toList();
    }

    public void discardCard(Card card) {
        discardedCards.add(card);
    }

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
            case RAW_MATERIAL_CARD -> getCardCount(CardType.RAW_MATERIAL);
            case MANUFACTURED_GOOD_CARD -> getCardCount(CardType.MANUFACTURED_GOOD);
            case MILITARY_CARD -> getCardCount(CardType.MILITARY);
            case SCIENCE_CARD -> getCardCount(CardType.SCIENCE);
            case COMMERCIAL_CARD -> getCardCount(CardType.COMMERCIAL);
            case CIVIL_CARD -> getCardCount(CardType.CIVIL);
            case WAR_LOSS -> getWarPointOfType(WarPoint.MINUS_ONE);
            case WAR_WIN_1 -> getWarPointOfType(WarPoint.ONE);
            case WAR_WIN_3 -> getWarPointOfType(WarPoint.THREE);
            case WAR_WIN_5 -> getWarPointOfType(WarPoint.FIVE);
            case WAR_WIN -> getWarPointOfType(null);
            case GUILD_CARD -> getCardCount(CardType.GUILD);
            case WONDER_STAGE -> {
                yield 0;
            } // TODO: implement
        };
    }

    private long getWarPointOfType(WarPoint warPointType) {
        return getWarPoints()
                .stream()
                .filter(warPoint -> (warPointType == null && warPoint.getValue() > 0) || warPoint.equals(warPointType))
                .count();
    }

    private long getCardCount(CardType cardType) {
        return Optional.ofNullable(getBuiltCards().stream().collect(groupingBy(Card::getType))
                        .get(cardType))
                .map(List::size)
                .orElse(0);
    }

    public String report() {
        var report = new ArrayList<String>();
        report.add("$" + coins);
        if (victoryPoints > 0) {
            report.add("V:" + victoryPoints);
        }
        if (shields > 0) {
            report.add("SH:" + shields);
        }
        if (!warPoints.isEmpty()) {
            report.add("W:" + warPoints.stream()
                    .map(WarPoint::name)
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }
        if (!builtCards.isEmpty()) {
            report.add("\nBuilt cards(%s): \n".formatted(builtCards.size()) + builtCards.stream()
                    .map(Card::report)
                    .collect(Collectors.joining("\n")));
        }
        return "\nVault\n%s".formatted(String.join(" ", report));
    }
}
