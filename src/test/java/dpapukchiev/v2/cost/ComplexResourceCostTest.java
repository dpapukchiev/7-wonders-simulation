package dpapukchiev.v2.cost;

import dpapukchiev.v2.BasePlayerTest;
import dpapukchiev.v2.game.TurnContext;
import dpapukchiev.v2.player.Player;
import dpapukchiev.v2.resources.ManufacturedGood;
import dpapukchiev.v2.resources.RawMaterial;
import dpapukchiev.v2.resources.ResourceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

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