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
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

import forestry.core.inventory.iterators.IInvSlot;
import forestry.core.inventory.iterators.InventoryIterator;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemHandlerInventoryManipulator extends InventoryManipulator<IInvSlot> {

	private final IItemHandler inv;

	protected ItemHandlerInventoryManipulator(IItemHandler inv) {
		this.inv = inv;
	}

	@Override
	public Iterator<IInvSlot> iterator() {
		return InventoryIterator.getIterable(inv).iterator();
	}

	@Override
	protected ItemStack addStack(ItemStack stack, boolean doAdd) {
		if (stack == null || stack.stackSize <= 0) {
			return null;
		}
		stack = stack.copy();
		List<IInvSlot> filledSlots = new ArrayList<>(inv.getSlots());
		List<IInvSlot> emptySlots = new ArrayList<>(inv.getSlots());
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
			ItemStack stackToInsert = stack.copy();
			stackToInsert.stackSize = stack.stackSize - injected;
			ItemStack remainder = inv.insertItem(slot.getIndex(), stackToInsert, !doAdd);
			if (remainder == null) {
				return injected;
			}
			injected += remainder.stackSize;
			if (injected >= stack.stackSize) {
				return injected;
			}
		}
		return injected;
	}
}
