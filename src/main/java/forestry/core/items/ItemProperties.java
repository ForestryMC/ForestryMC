package forestry.core.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import forestry.core.ItemGroupForestry;

public class ItemProperties extends Item.Properties {
    public int burnTime = -1;

    public ItemProperties(ItemGroup group) {
        group(group);
    }

    public ItemProperties() {
        this(ItemGroupForestry.tabForestry);
    }

    public ItemProperties burnTime(int burnTime) {
        this.burnTime = burnTime;
        return this;
    }
}
