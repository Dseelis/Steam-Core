package steamcore.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class SwordPart2Item extends Item {
	public SwordPart2Item() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
	}
}