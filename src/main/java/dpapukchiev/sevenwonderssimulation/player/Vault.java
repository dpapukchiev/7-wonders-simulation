package dpapukchiev.sevenwonderssimulation.player;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardName;
import dpapukchiev.sevenwonderssimulation.cards.CardType;
import dpapukchiev.sevenwonderssimulation.cards.Deck;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType;
import dpapukchiev.sevenwonderssimulation.effects.core.SpecialAction;
import dpapukchiev.sevenwonderssimulation.wonder.WonderContext;
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
    private WonderContext       wonderContext;
    private Deck                deck;
    @Builder.Default
    private List<WarPoint>      warPoints               = new ArrayList<>();
    @Builder.Default
    private double              victoryPoints           = 0;
    @Builder.Default
    private double              shields                 = 0;
    @Builder.Default
    private double              coins                   = 3;
    @Builder.Default
    private List<Card>          builtCards              = new ArrayList<>();
    @Builder.Default
    private List<Card>          discardedCards          = new ArrayList<>();
    @Builder.Default
    private List<SpecialAction> availableSpecialActions = new ArrayList<>();

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
        if (!availableSpecialActions.isEmpty()) {
            report.add("SA:" + availableSpecialActions.stream()
                    .map(SpecialAction::name)
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

    public void addSpecialActions(SpecialAction... specialActions) {
        availableSpecialActions.addAll(List.of(specialActions));
    }

    public Optional<SpecialAction> getSpecialAction(SpecialAction specialAction) {
        return availableSpecialActions.stream()
                .filter(action -> action.equals(specialAction))
                .findFirst();
    }

    public void useSpecialAction(SpecialAction specialAction) {
        var removed = availableSpecialActions.remove(specialAction);
        if(!removed) {
            throw new IllegalStateException("Special action %s is not available".formatted(specialAction));
        }
    }

    private long countCards(EffectMultiplierType effectMultiplierType) {
        return switch (effectMultiplierType) {
            case ONE -> 1;
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
            case WONDER_STAGE -> wonderContext.getBuiltStageCount();
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
}
