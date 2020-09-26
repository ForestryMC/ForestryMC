/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.factory.inventory;

import forestry.core.fluids.FluidHelper;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.factory.tiles.TileBottler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class InventoryBottler extends InventoryAdapterTile<TileBottler> {
    public static final short SLOT_INPUT_FULL_CONTAINER = 0;
    public static final short SLOT_INPUT_EMPTY_CONTAINER = 1;
    public static final short SLOT_EMPTYING_PROCESSING = 2;
    public static final short SLOT_FILLING_PROCESSING = 3;
    public static final short SLOT_OUTPUT_EMPTY_CONTAINER = 4;
    public static final short SLOT_OUTPUT_FULL_CONTAINER = 5;

    public InventoryBottler(TileBottler tileBottler) {
        super(tileBottler, 6, "Items");
    }

    @Override
    public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
        if (slotIndex == SLOT_INPUT_EMPTY_CONTAINER) {
            return FluidHelper.isFillableContainerWithRoom(itemStack);
        } else if (slotIndex == SLOT_INPUT_FULL_CONTAINER) {
            Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(itemStack);
            return fluidStack.map(f -> ForgeRegistries.FLUIDS.containsValue(f.getFluid())).orElse(false);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slotIndex, ItemStack itemstack, Direction side) {
        return slotIndex == SLOT_OUTPUT_EMPTY_CONTAINER || slotIndex == SLOT_OUTPUT_FULL_CONTAINER;
    }
}
