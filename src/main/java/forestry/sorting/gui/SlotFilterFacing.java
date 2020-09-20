package forestry.sorting.gui;

import com.mojang.datafixers.util.Pair;
import forestry.core.config.Constants;
import forestry.core.gui.slots.ISlotTextured;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class SlotFilterFacing extends Slot implements ISlotTextured {
    public SlotFilterFacing(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getBackground() {
        return Pair.of(
                PlayerContainer.LOCATION_BLOCKS_TEXTURE,
                new ResourceLocation(Constants.MOD_ID, "slots/bee")
        );
    }
}
