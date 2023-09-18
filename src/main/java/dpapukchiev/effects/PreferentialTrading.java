package dpapukchiev.effects;

import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;

import java.util.List;

public record PreferentialTrading(
        PreferentialTradingType type,
        List<ManufacturedGood> manufacturedGoods,
        List<RawMaterial> rawMaterials
) {
    public enum PreferentialTradingType {
        LEFT,
        RIGHT,
        BOTH
    }
}
