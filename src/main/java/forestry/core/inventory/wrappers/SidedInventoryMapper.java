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

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * Wrapper class used to bake the side variable into the object itself instead
 * of passing it around to all the inventory tools.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryMapper extends InvWrapperBase implements ISidedInventory {

	private final ISidedInventory inv;
	private final Direction side;

	public SidedInventoryMapper(ISidedInventory inv, Direction side) {
		this(inv, side, true);
	}

	public SidedInventoryMapper(ISidedInventory inv, Direction side, boolean checkItems) {
		super(inv, checkItems);
		this.inv = inv;
		this.side = side;
	}

	@Override
	public boolean isEmpty() {
		int[] slotsForFace = inv.getSlotsForFace(side);
		for (int slot : slotsForFace) {
			if (!inv.getStackInSlot(slot).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return inv.getSlotsForFace(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, Direction s) {
		return !checkItems() || inv.canInsertItem(slot, stack, side);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, Direction s) {
		return !checkItems() || inv.canExtractItem(slot, stack, side);
	}

}
