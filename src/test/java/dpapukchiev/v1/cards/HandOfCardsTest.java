package dpapukchiev.v1.cards;

import dpapukchiev.v1.cost.Cost;
import dpapukchiev.v1.cost.CostReport;
import dpapukchiev.v1.game.TurnContext;
import dpapukchiev.v1.player.BasePlayerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandOfCardsTest extends BasePlayerTest {
    @Mock
    private Cost costCovered;
    @Mock
    private Cost costNotCovered;

    @BeforeEach
    void init() {
        initPlayers();
        when(costCovered.generateCostReport(any()))
                .thenReturn(CostReport.builder().affordable(true).build());
        when(costNotCovered.generateCostReport(any()))
                .thenReturn(CostReport.builder().affordable(false).build());
    }

    @Test
    void getBuildableCards() {
        var card1 = new SingleResourceCard(1, "1", RawMaterial.WOOD, 3);
        var card2 = new SingleResourceCard(1, "2", RawMaterial.METAL_ORE, 3);
        var card3 = new SingleResourceCard(1, "3", RawMaterial.CLAY, 3);
        var card4 = new SingleResourceCard(1, "3", RawMaterial.STONE, 3);

        card1.setCost(costCovered);
        card2.setCost(costCovered);
        card3.setCost(costNotCovered);
        card4.setCost(costNotCovered);

        var handOfCards = HandOfCards.builder()
                .uuid(UUID.randomUUID())
                .cards(List.of(
                        card1,
                        card2,
                        card3,
                        card4
                ))
                .build();

        var turnContext = TurnContext.builder()
                .player(player1)
                .age(1)
                .simulationStep(2)
                .handOfCards(handOfCards)
                .build();

        var buildableCards = handOfCards.getBuildableCards(turnContext);

        assertEquals(2, buildableCards.size());
        assertTrue(buildableCards.containsAll(List.of(card1, card2)));
    }

}