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
package forestry.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class InventoryAdapterRestricted extends InventoryAdapter {
    public InventoryAdapterRestricted(int size, String name) {
        super(size, name);
    }

    public InventoryAdapterRestricted(int size, String name, int stackLimit) {
        super(size, name, stackLimit);
    }

    @Override
    public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
        return !itemStack.isEmpty() && canSlotAccept(slotIndex, itemStack);
    }

    @Override
    public final boolean canInsertItem(int slotIndex, ItemStack itemStack, Direction side) {
        return !itemStack.isEmpty() && isItemValidForSlot(slotIndex, itemStack);
    }

    @Override
    public boolean canExtractItem(int slotIndex, ItemStack itemStack, Direction side) {
        return !itemStack.isEmpty();
    }
}
