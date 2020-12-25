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
package forestry.factory.inventory;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.factory.tiles.TileRaintank;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class InventoryRaintank extends InventoryAdapterTile<TileRaintank> {
    public static final short SLOT_RESOURCE = 0;
    public static final short SLOT_PRODUCT = 1;

    public InventoryRaintank(TileRaintank raintank) {
        super(raintank, 3, "Items");
    }

    @Override
    public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
        if (slotIndex == SLOT_RESOURCE) {
            LazyOptional<IFluidHandlerItem> fluidHandler = FluidUtil.getFluidHandler(itemStack);
            return fluidHandler.map(handler ->
                    handler.fill(new FluidStack(Fluids.WATER, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE) >
                    0).orElse(false);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slotIndex, ItemStack itemstack, Direction side) {
        return slotIndex == SLOT_PRODUCT;
    }
}
