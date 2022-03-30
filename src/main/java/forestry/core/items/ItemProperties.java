package forestry.core.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;

import forestry.core.ItemGroupForestry;

public class ItemProperties extends Item.Properties {
	public int burnTime = -1;

	public ItemProperties(CreativeModeTab group) {
		tab(group);
	}

	public ItemProperties() {
		this(ItemGroupForestry.tabForestry);
	}

	public ItemProperties burnTime(int burnTime) {
		this.burnTime = burnTime;
		return this;
	}
}
