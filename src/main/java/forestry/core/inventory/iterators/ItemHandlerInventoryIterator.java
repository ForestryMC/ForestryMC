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

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

import forestry.core.utils.InventoryUtil;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemHandlerInventoryIterator extends InventoryIterator<IInvSlot> {

	private final IItemHandler inv;

	protected ItemHandlerInventoryIterator(IItemHandler inv) {
		this.inv = inv;
	}

	@Override
	public Iterator<IInvSlot> iterator() {
		return new Iterator<IInvSlot>() {
			int slot = 0;

			@Override
			public boolean hasNext() {
				return slot < inv.getSlots();
			}

			@Override
			public IInvSlot next() {
				return new InvSlot(slot++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Remove not supported.");
			}

		};
	}

	private class InvSlot implements IInvSlot {

		protected final int slot;

		public InvSlot(int slot) {
			this.slot = slot;
		}

		@Override
		public int getIndex() {
			return slot;
		}

		@Override
		public boolean canPutStackInSlot(ItemStack stack) {
			return inv.insertItem(slot, stack, true) == null;
		}

		@Override
		public boolean canTakeStackFromSlot(ItemStack stack) {
			return inv.extractItem(slot, 1, true) != null;
		}

		@Override
		public ItemStack decreaseStackInSlot() {
			return inv.extractItem(slot, 1, false);
		}

		@Override
		public ItemStack getStackInSlot() {
			return InventoryUtil.makeSafe(inv.getStackInSlot(slot));
		}

	}
}
