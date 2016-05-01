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
package forestry.core.inventory.iterators;

import java.util.Iterator;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryIterator extends StandardInventoryIterator {

	private final ISidedInventory inv;

	protected SidedInventoryIterator(ISidedInventory inv) {
		super(inv);
		this.inv = inv;
	}

	@Override
	public Iterator<IExtInvSlot> iterator() {
		return new Iterator<IExtInvSlot>() {
			int[] slots = inv.getSlotsForFace(EnumFacing.DOWN);
			int index = 0;

			@Override
			public boolean hasNext() {
				return slots != null && index < slots.length;
			}

			@Override
			public IExtInvSlot next() {
				return new InvSlot(slots[index++]);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Remove not supported.");
			}

		};
	}

	private class InvSlot extends StandardInventoryIterator.InvSlot implements IExtInvSlot {

		public InvSlot(int slot) {
			super(slot);
		}

		@Override
		public boolean canPutStackInSlot(ItemStack stack) {
			return inv.canInsertItem(slot, stack, EnumFacing.DOWN);
		}

		@Override
		public boolean canTakeStackFromSlot(ItemStack stack) {
			return inv.canExtractItem(slot, stack, EnumFacing.DOWN);
		}

	}
}
