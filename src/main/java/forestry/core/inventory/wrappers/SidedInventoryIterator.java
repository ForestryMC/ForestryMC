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

import java.util.Iterator;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryIterator implements Iterable<IInvSlot> {

	private final ISidedInventory inv;

	SidedInventoryIterator(ISidedInventory inv) {
		this.inv = inv;
	}

	@Override
	public Iterator<IInvSlot> iterator() {
		return new Iterator<IInvSlot>() {
			final int[] slots = inv.getAccessibleSlotsFromSide(0);
			int index = 0;

			@Override
			public boolean hasNext() {
				return slots != null && index < slots.length;
			}

			@Override
			public IInvSlot next() {
				return new InvSlot(slots[index++]);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Remove not supported.");
			}

		};
	}

	private class InvSlot implements IInvSlot {

		private final int slot;

		public InvSlot(int slot) {
			this.slot = slot;
		}

		@Override
		public ItemStack getStackInSlot() {
			return inv.getStackInSlot(slot);
		}

		@Override
		public void setStackInSlot(ItemStack stack) {
			inv.setInventorySlotContents(slot, stack);
		}

		@Override
		public boolean canPutStackInSlot(ItemStack stack) {
			return inv.canInsertItem(slot, stack, 0);
		}

		@Override
		public boolean canTakeStackFromSlot(ItemStack stack) {
			return inv.canExtractItem(slot, stack, 0);
		}

		@Override
		public ItemStack decreaseStackInSlot() {
			return inv.decrStackSize(slot, 1);
		}

		@Override
		public int getIndex() {
			return slot;
		}

	}
}
