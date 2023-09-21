package dpapukchiev.sevenwonderssimulation.player;

import dpapukchiev.sevenwonderssimulation.cards.Card;
import dpapukchiev.sevenwonderssimulation.cards.CardType;
import dpapukchiev.sevenwonderssimulation.effects.core.EffectMultiplierType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VaultTest {

    private Vault vault;

    @BeforeEach
    void setUp() {
        vault = new Vault();
    }

    @Test
    void addWarPoint() {
        vault.addWarPoint(WarPoint.MINUS_ONE);
        vault.addWarPoint(WarPoint.MINUS_ONE);
        vault.addWarPoint(WarPoint.MINUS_ONE);
        vault.addWarPoint(WarPoint.THREE);
        vault.addWarPoint(WarPoint.FIVE);

        assertEquals(5, vault.getWarPointsScore());
    }

    @Test
    void addVictoryPoints() {
        vault.addVictoryPoints(2);
        vault.addVictoryPoints(2);
        vault.addVictoryPoints(5);

        assertEquals(9, vault.getVictoryPoints());
    }

    @Test
    void addShields() {
        vault.addShields(2);
        vault.addShields(3);

        assertEquals(5, vault.getShields());
    }

    @Test
    void addCoins() {
        vault.addCoins(2);
        vault.addCoins(3);

        assertEquals(8, vault.getCoins());
    }

    @Test
    void removeCoins() {
        vault.addCoins(2);
        vault.addCoins(3);
        vault.removeCoins(2);

        assertEquals(6, vault.getCoins());
    }

    @Test
    void countMultiplierCards() {
        EffectMultiplierType.getCardTypes()
                .forEach(effectMultiplierType -> {
                    for (int i = 0; i < 3; i++) {
                        vault.addBuiltCard(Card.builder().type(getCardTypeForMultiplier(effectMultiplierType)).build());
                        assertEquals(i + 1, vault.countMultiplier(effectMultiplierType));
                    }
                });
    }

    @Test
    void countMultiplier() {
        vault.addBuiltCard(Card.builder().type(getCardTypeForMultiplier(EffectMultiplierType.SCIENCE_CARD)).build());
        assertEquals(1, vault.countMultiplier(EffectMultiplierType.SCIENCE_CARD));

        vault.addBuiltCard(Card.builder().type(getCardTypeForMultiplier(EffectMultiplierType.RAW_MATERIAL_CARD)).build());
        assertEquals(1, vault.countMultiplier(EffectMultiplierType.SCIENCE_CARD));
    }

    @Test
    void countMultiplierWar() {
        assertEquals(0, vault.countMultiplier(EffectMultiplierType.WAR_LOSS));
        assertEquals(0, vault.countMultiplier(EffectMultiplierType.WAR_WIN_1));
        assertEquals(0, vault.countMultiplier(EffectMultiplierType.WAR_WIN_3));
        assertEquals(0, vault.countMultiplier(EffectMultiplierType.WAR_WIN_5));
        assertEquals(0, vault.countMultiplier(EffectMultiplierType.WAR_WIN));

        vault.addWarPoint(WarPoint.MINUS_ONE);

        vault.addWarPoint(WarPoint.ONE);
        vault.addWarPoint(WarPoint.THREE);
        vault.addWarPoint(WarPoint.FIVE);

        assertEquals(1, vault.countMultiplier(EffectMultiplierType.WAR_LOSS));
        assertEquals(1, vault.countMultiplier(EffectMultiplierType.WAR_WIN_1));
        assertEquals(1, vault.countMultiplier(EffectMultiplierType.WAR_WIN_3));
        assertEquals(1, vault.countMultiplier(EffectMultiplierType.WAR_WIN_5));
        assertEquals(3, vault.countMultiplier(EffectMultiplierType.WAR_WIN));
    }

    @NotNull
    private static CardType getCardTypeForMultiplier(EffectMultiplierType multiplierType) {
        return switch (multiplierType) {
            case RAW_MATERIAL_CARD -> CardType.RAW_MATERIAL;
            case MANUFACTURED_GOOD_CARD -> CardType.MANUFACTURED_GOOD;
            case SCIENCE_CARD -> CardType.SCIENCE;
            case CIVIL_CARD -> CardType.CIVIL;
            case COMMERCIAL_CARD -> CardType.COMMERCIAL;
            case MILITARY_CARD -> CardType.MILITARY;
            default -> throw new IllegalStateException("Unexpected value: " + multiplierType);
        };
    }
}