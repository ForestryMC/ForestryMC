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
package forestry.core.inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemHandlerInventoryManipulator implements Iterable<IInvSlot> {

	private final IItemHandler inv;

	public ItemHandlerInventoryManipulator(IItemHandler inv) {
		this.inv = inv;
	}

	@Override
	public Iterator<IInvSlot> iterator() {
		return new InvIterator(inv);
	}

	/**
	 * Simulate adding the stack to the inventory.
	 *
	 * @return the remainder
	 */
	@Nullable
	public ItemStack tryAddStack(ItemStack stack) {
		return addStack(stack, false);
	}

	/**
	 * Attempt to add the stack to the inventory.
	 *
	 * @return The remainder
	 */
	@Nullable
	public ItemStack addStack(ItemStack stack) {
		return addStack(stack, true);
	}

	/**
	 * Removed an item matching the filter.
	 */
	public ItemStack removeItem(Predicate<ItemStack> filter) {
		for (IInvSlot slot : this) {
			ItemStack stack = slot.getStackInSlot();
			if (!stack.isEmpty() && slot.canTakeStackFromSlot(stack) && filter.test(stack)) {
				return slot.decreaseStackInSlot();
			}
		}
		return ItemStack.EMPTY;
	}

	public boolean transferOneStack(IItemHandler dest, Predicate<ItemStack> filter) {
		return transferStacks(dest, filter, true);
	}

	public boolean transferStacks(IItemHandler dest, Predicate<ItemStack> filter) {
		return transferStacks(dest, filter, false);
	}

	private boolean transferStacks(IItemHandler dest, Predicate<ItemStack> filter, boolean singleStack) {
		ItemHandlerInventoryManipulator destManipulator = new ItemHandlerInventoryManipulator(dest);
		boolean stacksMoved = false;
		for (int slotIndex = 0; slotIndex < inv.getSlots(); slotIndex++) {
			ItemStack targetStack = inv.extractItem(slotIndex, Integer.MAX_VALUE, true);
			if (!targetStack.isEmpty() && filter.test(targetStack)) {
				int extractStackSize = targetStack.getCount();
				ItemStack remaining = destManipulator.tryAddStack(targetStack);
				if (remaining != null) {
					extractStackSize -= remaining.getCount();
				}
				if (extractStackSize > 0) {
					ItemStack extracted = inv.extractItem(slotIndex, extractStackSize, false);
					destManipulator.addStack(extracted);
					stacksMoved = true;
					if (singleStack) {
						return true;
					}
				}
			}
		}
		return stacksMoved;
	}

	@Nullable
	protected ItemStack addStack(ItemStack stack, boolean doAdd) {
		if (stack.isEmpty()) {
			return null;
		}
		stack = stack.copy();
		List<IInvSlot> filledSlots = new ArrayList<>(inv.getSlots());
		List<IInvSlot> emptySlots = new ArrayList<>(inv.getSlots());
		for (IInvSlot slot : new ItemHandlerInventoryManipulator(inv)) {
			if (slot.canPutStackInSlot(stack)) {
				if (slot.getStackInSlot().isEmpty()) {
					emptySlots.add(slot);
				} else {
					filledSlots.add(slot);
				}
			}
		}

		int injected = 0;
		injected = tryPut(filledSlots, stack, injected, doAdd);
		injected = tryPut(emptySlots, stack, injected, doAdd);
		stack.shrink(injected);
		return stack;
	}

	private int tryPut(List<IInvSlot> slots, ItemStack stack, int injected, boolean doAdd) {
		if (injected >= stack.getCount()) {
			return injected;
		}
		for (IInvSlot slot : slots) {
			final ItemStack stackToInsert = stack.copy();
			final int stackToInsertSize = stack.getCount() - injected;
			stackToInsert.setCount(stackToInsertSize);

			final ItemStack remainder = inv.insertItem(slot.getIndex(), stackToInsert, !doAdd);
			if (remainder.isEmpty()) {
				return stack.getCount();
			}
			injected += stackToInsertSize - remainder.getCount();
			if (injected >= stack.getCount()) {
				return injected;
			}
		}
		return injected;
	}
}
