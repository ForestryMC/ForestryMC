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
package forestry.core.inventory.wrappers;

import forestry.core.inventory.InventoryPlain;
import forestry.core.inventory.iterators.IExtInvSlot;
import forestry.core.inventory.iterators.InventoryIterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Creates a deep copy of an existing IInventory.
 * <p/>
 * Useful for performing inventory manipulations and then examining the results
 * without affecting the original inventory.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryCopy extends InvWrapperBase {

    private InventoryPlain copy;

    public InventoryCopy(IInventory original) {
        super(original);
        this.copy = new InventoryPlain(original.getSizeInventory());
        for (IExtInvSlot slot : InventoryIterator.getIterable(original)) {
            ItemStack stack = slot.getStackInSlot();
            if (stack != null) {
                copy.setInventorySlotContents(slot.getIndex(), stack.copy());
            }
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        copy.setInventorySlotContents(slot, itemstack);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return copy.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return copy.decrStackSize(slot, amount);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return copy.removeStackFromSlot(slot);
    }

    @Override
	public void markDirty() {
    }
}
