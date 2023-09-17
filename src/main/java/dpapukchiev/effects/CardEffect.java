package dpapukchiev.effects;

import dpapukchiev.cards.Card;
import dpapukchiev.cards.ManufacturedGood;
import dpapukchiev.cards.RawMaterial;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CardEffect {
    protected EffectUsageType effectUsageType;
    protected int maxUsages;
    protected int usedCount;
    protected double coinReward;
    protected double pointsAward;
    protected ManufacturedGood providedManufacturedGood;
    protected boolean wildcardRawMaterial;
    protected List<RawMaterial> providedRawMaterials;
    protected List<Card> freeCards;
}
