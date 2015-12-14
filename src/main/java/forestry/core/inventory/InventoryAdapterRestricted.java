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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.core.access.IAccessHandler;
import forestry.core.config.Constants;

public class InventoryAdapterRestricted extends InventoryAdapter {
	private final IAccessHandler accessHandler;

	public InventoryAdapterRestricted(int size, String name, IAccessHandler accessHandler) {
		super(size, name);
		this.accessHandler = accessHandler;
	}

	public InventoryAdapterRestricted(int size, String name, int stackLimit, IAccessHandler accessHandler) {
		super(size, name, stackLimit);
		this.accessHandler = accessHandler;
	}

	@Override
	public final boolean isUseableByPlayer(EntityPlayer player) {
		return accessHandler.allowsViewing(player);
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if (itemStack == null || !accessHandler.allowsPipeConnections()) {
			return false;
		}

		return canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (!accessHandler.allowsPipeConnections()) {
			return Constants.SLOTS_NONE;
		}
		return super.getAccessibleSlotsFromSide(side);
	}

	@Override
	public final boolean canInsertItem(int slotIndex, ItemStack itemStack, int side) {
		if (itemStack == null || !accessHandler.allowsPipeConnections()) {
			return false;
		}
		return isItemValidForSlot(slotIndex, itemStack);
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		return itemStack != null && accessHandler.allowsPipeConnections();
	}
}
