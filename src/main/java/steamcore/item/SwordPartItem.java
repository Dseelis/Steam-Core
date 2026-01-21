package steamcore.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class SwordPartItem extends Item {
	public SwordPartItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
	}
}