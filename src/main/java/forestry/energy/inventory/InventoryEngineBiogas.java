/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.energy.inventory;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.energy.tiles.TileEngineBiogas;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Optional;

public class InventoryEngineBiogas extends InventoryAdapterTile<TileEngineBiogas> {
    public static final short SLOT_CAN = 0;

    public InventoryEngineBiogas(TileEngineBiogas engineBronze) {
        super(engineBronze, 1, "Items");
    }

    @Override
    public boolean canExtractItem(int slotIndex, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
        if (slotIndex == SLOT_CAN) {
            Optional<FluidStack> fluid = FluidUtil.getFluidContained(itemStack);
            return fluid.map(f -> tile.getTankManager().canFillFluidType(f)).orElse(false);
        }

        return false;
    }
}
