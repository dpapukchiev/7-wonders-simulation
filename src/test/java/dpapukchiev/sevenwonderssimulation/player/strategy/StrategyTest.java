package dpapukchiev.sevenwonderssimulation.player.strategy;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardName;
import dpapukchiev.sevenwonderssimulation.cards.HandOfCards;
import dpapukchiev.sevenwonderssimulation.cost.Cost;
import dpapukchiev.sevenwonderssimulation.cost.CostReport;
import dpapukchiev.sevenwonderssimulation.effects.core.Effect;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.player.Vault;
import dpapukchiev.sevenwonderssimulation.wonder.WonderContext;
import dpapukchiev.sevenwonderssimulation.wonder.WonderStage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StrategyTest {
    @Mock
    private Player        player;
    @Mock
    private Vault         vault;
    @Mock
    private WonderContext wonderContext;
    @Mock
    private TurnContext   turnContext;
    @Mock
    private HandOfCards   handOfCards;

    @Mock
    private Player leftNeighbour;
    @Mock
    private Vault  leftNeighbourVault;
    @Mock
    private Player rightNeighbour;
    @Mock
    private Vault  rightNeighbourVault;

    @Mock
    private WonderStage wonderStage;
    @Mock
    private Effect      wonderStageEffect;
    @Mock
    private Cost        wonderStageCost;
    @Mock
    private CostReport  wonderStageCostReport;

    @Mock
    private Card       card;
    @Mock
    private Cost       cardCost;
    @Mock
    private Effect     cardEffect;
    @Mock
    private CostReport cardCostReport;

    @BeforeEach
    void setUp() {
        when(turnContext.getHandOfCards()).thenReturn(handOfCards);
        when(turnContext.getPlayer()).thenReturn(player);
        lenient().when(player.getWonderContext()).thenReturn(wonderContext);
        lenient().when(player.getVault()).thenReturn(vault);

        when(player.selectRandomCard(anyList())).thenReturn(card);

        lenient().when(wonderStage.getCost()).thenReturn(wonderStageCost);
        lenient().when(wonderStage.getEffect()).thenReturn(wonderStageEffect);
        lenient().when(card.report()).thenReturn("card-report");
        lenient().when(card.getCost()).thenReturn(cardCost);
        lenient().when(card.getEffect()).thenReturn(cardEffect);
        lenient().when(card.getName()).thenReturn(CardName.PALAST);

        lenient().when(player.getLeftPlayer()).thenReturn(leftNeighbour);
        lenient().when(leftNeighbour.getVault()).thenReturn(leftNeighbourVault);
        lenient().when(player.getRightPlayer()).thenReturn(rightNeighbour);
        lenient().when(rightNeighbour.getVault()).thenReturn(rightNeighbourVault);
        lenient().when(cardCost.generateCostReport(turnContext)).thenReturn(cardCostReport);
    }

    @Test
    void discard() {
        when(wonderContext.getNextWonderStage(turnContext))
                .thenReturn(Optional.empty());

        var strategy = getStrategy();

        strategy.execute(turnContext);

        verify(vault, times(1)).addCoins(3);
        verify(handOfCards, times(1)).discard(card);
        verify(vault, times(1)).discardCard(card);
    }

    @Test
    void wonder() {
        when(wonderContext.getNextWonderStage(turnContext))
                .thenReturn(Optional.of(wonderStage));
        when(wonderStageCostReport.isAffordable()).thenReturn(true);
        when(wonderStageCost.generateCostReport(turnContext))
                .thenReturn(wonderStageCostReport);
        when(wonderStageCostReport.getToPayTotal()).thenReturn(3d);
        when(wonderStageCostReport.getToPayLeft()).thenReturn(1d);
        when(wonderStageCostReport.getToPayRight()).thenReturn(2d);

        var strategy = getStrategy();

        strategy.execute(turnContext);

        verify(wonderStageEffect, times(1)).scheduleRewardEvaluationAndCollection(player);
        verify(wonderStage, times(1)).build(card, turnContext);
        verify(handOfCards, times(1)).remove(card);

        verify(vault, times(1)).removeCoins(3);
        verify(leftNeighbourVault, times(1)).addCoins(1);
        verify(rightNeighbourVault, times(1)).addCoins(2);
    }

    @Test
    void noCost() {
        when(handOfCards.getCardsWithNoCost(turnContext))
                .thenReturn(List.of(card));

        var strategy = getStrategy();

        strategy.execute(turnContext);

        verify(cardEffect, times(1)).scheduleRewardEvaluationAndCollection(player);
        verify(handOfCards, times(1)).remove(card);
        verify(vault, times(1)).addBuiltCard(card);
    }

    @Test
    void someCost() {
        when(handOfCards.getCardsWithCost(turnContext))
                .thenReturn(List.of(card));
        when(cardCostReport.isAffordable()).thenReturn(true);
        when(cardCostReport.getToPayTotal()).thenReturn(5d);
        when(cardCostReport.getToPayLeft()).thenReturn(2d);
        when(cardCostReport.getToPayRight()).thenReturn(3d);

        var strategy = getStrategy();

        strategy.execute(turnContext);

        verify(cardEffect, times(1)).scheduleRewardEvaluationAndCollection(player);
        verify(handOfCards, times(1)).remove(card);
        verify(vault, times(1)).addBuiltCard(card);

        verify(vault, times(1)).removeCoins(5);
        verify(leftNeighbourVault, times(1)).addCoins(2);
        verify(rightNeighbourVault, times(1)).addCoins(3);
    }

    @NotNull
    private static Strategy getStrategy() {
        return Strategy.defaultStrategy();
    }
}