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
package forestry.apiculture.inventory;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IHiveFrame;
import forestry.apiculture.tiles.TileApiary;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.utils.SlotUtil;

public class InventoryApiary extends InventoryTileBeeHousing {
	public static final int SLOT_FRAMES_1 = 9;
	public static final int SLOT_FRAMES_COUNT = 3;

	public InventoryApiary(TileApiary tile) {
		super(tile, 12, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (SlotUtil.isSlotInRange(slotIndex, SLOT_FRAMES_1, SLOT_FRAMES_COUNT)) {
			return (itemStack.getItem() instanceof IHiveFrame) && (getStackInSlot(slotIndex) == null);
		}

		return super.canSlotAccept(slotIndex, itemStack);
	}

	// override for pipe automation
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if (SlotUtil.isSlotInRange(slotIndex, SLOT_FRAMES_1, SLOT_FRAMES_COUNT)) {
			return false;
		}
		return super.isItemValidForSlot(slotIndex, itemStack);
	}

	public IInventory getFrameInventory() {
		return new InventoryMapper(this, SLOT_FRAMES_1, SLOT_FRAMES_COUNT);
	}

	public Collection<IHiveFrame> getFrames() {
		Collection<IHiveFrame> hiveFrames = new ArrayList<>(SLOT_FRAMES_COUNT);

		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			ItemStack stackInSlot = getStackInSlot(i);
			if (stackInSlot == null) {
				continue;
			}

			Item itemInSlot = stackInSlot.getItem();
			if (itemInSlot instanceof IHiveFrame) {
				hiveFrames.add((IHiveFrame) itemInSlot);
			}
		}

		return hiveFrames;
	}
}
