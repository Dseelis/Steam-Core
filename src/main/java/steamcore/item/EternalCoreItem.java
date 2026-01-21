package steamcore.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class EternalCoreItem extends Item {
	public EternalCoreItem() {
		super(new Item.Properties().stacksTo(4).fireResistant().rarity(Rarity.EPIC));
	}
}