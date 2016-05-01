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
package forestry.core.inventory.manipulators;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

import forestry.core.inventory.InventoryObject;
import forestry.core.inventory.filters.IStackFilter;
import forestry.core.inventory.iterators.IExtInvSlot;
import forestry.core.inventory.iterators.IInvSlot;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryManipulator<T extends IInvSlot> implements Iterable<T> {

	public static InventoryManipulator<IExtInvSlot> get(IInventory inv) {
		return new StandardInventoryManipulator(inv);
	}

	public static InventoryManipulator<IInvSlot> get(IItemHandler inv) {
		return new ItemHandlerInventoryManipulator(inv);
	}

	public static InventoryManipulator<? extends IInvSlot> get(InventoryObject inv) {
		if (inv.getObject() instanceof IInventory) {
			return new StandardInventoryManipulator((IInventory) inv.getObject());
		}
		if (inv.getObject() instanceof IItemHandler) {
			return new ItemHandlerInventoryManipulator((IItemHandler) inv.getObject());
		}
		throw new RuntimeException("Invalid Inventory Object");
	}

	protected InventoryManipulator() {
	}

	public ItemStack tryAddStack(ItemStack stack) {
		return addStack(stack, false);
	}

	/**
	 * Attempt to add the stack to the inventory.
	 *
	 * @return The remainder
	 */
	public ItemStack addStack(ItemStack stack) {
		return addStack(stack, true);
	}

	protected abstract ItemStack addStack(ItemStack stack, boolean doAdd);

	/**
	 * Removed an item matching the filter.
	 */
	public ItemStack removeItem(IStackFilter filter) {
		for (IInvSlot slot : this) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null && stack.stackSize > 0 && slot.canTakeStackFromSlot(stack) && filter.apply(stack)) {
				return slot.decreaseStackInSlot();
			}
		}
		return null;
	}

}
