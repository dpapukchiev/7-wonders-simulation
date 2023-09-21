package dpapukchiev.sevenwonderssimulation.cost;

import dpapukchiev.sevenwonderssimulation.BasePlayerTest;
import dpapukchiev.sevenwonderssimulation.game.TurnContext;
import dpapukchiev.sevenwonderssimulation.player.Player;
import dpapukchiev.sevenwonderssimulation.resources.ManufacturedGood;
import dpapukchiev.sevenwonderssimulation.resources.RawMaterial;
import dpapukchiev.sevenwonderssimulation.resources.ResourceContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ComplexResourceCostTest extends BasePlayerTest {

    static Stream<Arguments> generateCostReportArguments() {
        var argumentsStream = Stream.<Arguments>builder();
        for (RawMaterial rawMaterial : RawMaterial.values()) {
            for (ManufacturedGood manufacturedGood : ManufacturedGood.values()) {
                argumentsStream.add(Arguments.of(rawMaterial, manufacturedGood));
            }
        }
        return argumentsStream.build();
    }

    @ParameterizedTest
    @MethodSource("generateCostReportArguments")
    void unitTest(RawMaterial rawMaterial, ManufacturedGood manufacturedGood) {
        var turnContext = mock(TurnContext.class);
        var resourceContext = mock(ResourceContext.class);
        var costReport = mock(CostReport.class);
        var player = mock(Player.class);

        when(turnContext.getPlayer()).thenReturn(player);
        when(player.resourceContext()).thenReturn(resourceContext);
        when(resourceContext.calculateResourcesCost(List.of(rawMaterial), List.of(manufacturedGood)))
                .thenReturn(costReport);

        var cost = new ComplexResourceCost(List.of(rawMaterial), List.of(manufacturedGood));

        var result = cost.generateCostReport(turnContext);

        assertEquals(costReport, result);
    }
}