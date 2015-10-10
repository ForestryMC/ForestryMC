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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import forestry.core.inventory.filters.IStackFilter;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.utils.InventoryUtil;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryManipulator {

	private final IInventory inv;

	public static InventoryManipulator get(IInventory inv) {
		return new InventoryManipulator(inv);
	}

	protected InventoryManipulator(IInventory inv) {
		this.inv = inv;
	}

	protected Iterable<IInvSlot> getSlots() {
		return InventoryIterator.getIterable(inv);
	}

	public boolean canAddStack(ItemStack stack) {
		return tryAddStack(stack) == null;
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

	private ItemStack addStack(ItemStack stack, boolean doAdd) {
		if (stack == null || stack.stackSize <= 0) {
			return null;
		}
		stack = stack.copy();
		List<IInvSlot> filledSlots = new ArrayList<>(inv.getSizeInventory());
		List<IInvSlot> emptySlots = new ArrayList<>(inv.getSizeInventory());
		for (IInvSlot slot : InventoryIterator.getIterable(inv)) {
			if (slot.canPutStackInSlot(stack)) {
				if (slot.getStackInSlot() == null) {
					emptySlots.add(slot);
				} else {
					filledSlots.add(slot);
				}
			}
		}

		int injected = 0;
		injected = tryPut(filledSlots, stack, injected, doAdd);
		injected = tryPut(emptySlots, stack, injected, doAdd);
		stack.stackSize -= injected;
		if (stack.stackSize <= 0) {
			return null;
		}
		return stack;
	}

	private int tryPut(List<IInvSlot> slots, ItemStack stack, int injected, boolean doAdd) {
		if (injected >= stack.stackSize) {
			return injected;
		}
		for (IInvSlot slot : slots) {
			ItemStack stackInSlot = slot.getStackInSlot();
			if (stackInSlot == null || InventoryUtil.isItemEqual(stackInSlot, stack)) {
				int used = addToSlot(slot, stack, stack.stackSize - injected, doAdd);
				if (used > 0) {
					injected += used;
					if (injected >= stack.stackSize) {
						return injected;
					}
				}
			}
		}
		return injected;
	}

	/**
	 * @param available Amount we can move
	 * @return Return the number of items moved.
	 */
	private int addToSlot(IInvSlot slot, ItemStack stack, int available, boolean doAdd) {
		int max = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());

		ItemStack stackInSlot = slot.getStackInSlot();
		if (stackInSlot == null) {
			int wanted = Math.min(available, max);
			if (doAdd) {
				stackInSlot = stack.copy();
				stackInSlot.stackSize = wanted;
				slot.setStackInSlot(stackInSlot);
			}
			return wanted;
		}

		if (!InventoryUtil.isItemEqual(stack, stackInSlot)) {
			return 0;
		}

		int wanted = max - stackInSlot.stackSize;
		if (wanted <= 0) {
			return 0;
		}

		if (wanted > available) {
			wanted = available;
		}

		if (doAdd) {
			stackInSlot.stackSize += wanted;
			slot.setStackInSlot(stackInSlot);
		}
		return wanted;
	}

	public boolean canRemoveItem(IStackFilter filter) {
		return tryRemoveItem(filter) == null;
	}

	public ItemStack tryRemoveItem(IStackFilter filter) {
		for (IInvSlot slot : getSlots()) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null && slot.canTakeStackFromSlot(stack) && filter.matches(stack)) {
				ItemStack output = stack.copy();
				output.stackSize = 1;
				return output;
			}
		}
		return null;
	}

	public ItemStack removeItem(IStackFilter filter) {
		for (IInvSlot slot : getSlots()) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null && slot.canTakeStackFromSlot(stack) && filter.matches(stack)) {
				return slot.decreaseStackInSlot();
			}
		}
		return null;
	}

	public ItemStack moveItem(IInventory dest, IStackFilter filter) {
		InventoryManipulator imDest = InventoryManipulator.get(dest);
		for (IInvSlot slot : getSlots()) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null && slot.canTakeStackFromSlot(stack) && filter.matches(stack)) {
				stack = stack.copy();
				stack.stackSize = 1;
				stack = imDest.addStack(stack);
				if (stack == null) {
					return slot.decreaseStackInSlot();
				}
			}
		}
		return null;
	}

}
