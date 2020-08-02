package forestry.sorting.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.core.config.Constants;
import forestry.core.gui.slots.ISlotTextured;

public class SlotFilterFacing extends Slot implements ISlotTextured {

    public SlotFilterFacing(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ResourceLocation getBackgroundTexture() {
        return new ResourceLocation(Constants.MOD_ID, "slots/bee");
    }
}
