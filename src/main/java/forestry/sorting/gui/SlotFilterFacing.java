package forestry.sorting.gui;

import com.mojang.datafixers.util.Pair;
import forestry.core.config.Constants;
import forestry.core.render.TextureManagerForestry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class SlotFilterFacing extends Slot {

	public SlotFilterFacing(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

    @Nullable
	@Override
    public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
        return Pair.of(TextureManagerForestry.LOCATION_FORESTRY_TEXTURE, new ResourceLocation(Constants.MOD_ID, "slots/bee"));
	}
}
