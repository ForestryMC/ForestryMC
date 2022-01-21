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

import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

/**
 * Wrapper class used to bake the side variable into the object itself instead
 * of passing it around to all the inventory tools.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryMapper extends InvWrapperBase implements WorldlyContainer {

	private final WorldlyContainer inv;
	private final Direction side;

	public SidedInventoryMapper(WorldlyContainer inv, Direction side) {
		this(inv, side, true);
	}

	public SidedInventoryMapper(WorldlyContainer inv, Direction side, boolean checkItems) {
		super(inv, checkItems);
		this.inv = inv;
		this.side = side;
	}

	@Override
	public boolean isEmpty() {
		int[] slotsForFace = inv.getSlotsForFace(side);
		for (int slot : slotsForFace) {
			if (!inv.getItem(slot).isEmpty()) {
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
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction s) {
		return !checkItems() || inv.canPlaceItemThroughFace(slot, stack, side);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction s) {
		return !checkItems() || inv.canTakeItemThroughFace(slot, stack, side);
	}

}
