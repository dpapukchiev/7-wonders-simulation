package dpapukchiev.effects;

import dpapukchiev.cards.Card;
import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardEffect {
    protected EffectUsageType effectUsageType;
    protected int maxUsages;
    protected int usedCount;
    protected double coinReward;
    protected double pointsAward;
    protected boolean wildcardManufacturedGood;
    protected List<ManufacturedGood> providedManufacturedGoods;
    protected boolean wildcardRawMaterial;
    protected List<RawMaterial> providedRawMaterials;
    protected List<Card> freeCards;
}
