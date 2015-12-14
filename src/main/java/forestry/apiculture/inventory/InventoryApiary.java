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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.IHiveFrame;
import forestry.apiculture.InventoryBeeHousing;
import forestry.core.access.IAccessHandler;
import forestry.core.utils.SlotUtil;

public class InventoryApiary extends InventoryBeeHousing implements IApiaryInventory {
	public static final int SLOT_FRAMES_1 = 9;
	public static final int SLOT_FRAMES_COUNT = 3;

	public InventoryApiary(IAccessHandler accessHandler) {
		super(12, accessHandler);
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

	public void wearOutFrames(IBeeHousing beeHousing, int amount) {
		IBeekeepingMode beekeepingMode = BeeManager.beeRoot.getBeekeepingMode(beeHousing.getWorld());
		int wear = Math.round(amount * beekeepingMode.getWearModifier());

		for (int i = SLOT_FRAMES_1; i < SLOT_FRAMES_1 + SLOT_FRAMES_COUNT; i++) {
			ItemStack hiveFrameStack = getStackInSlot(i);
			if (hiveFrameStack == null) {
				continue;
			}

			Item hiveFrameItem = hiveFrameStack.getItem();
			if (!(hiveFrameItem instanceof IHiveFrame)) {
				continue;
			}

			IHiveFrame hiveFrame = (IHiveFrame) hiveFrameItem;

			ItemStack queenStack = getQueen();
			IBee queen = BeeManager.beeRoot.getMember(queenStack);
			ItemStack usedFrame = hiveFrame.frameUsed(beeHousing, hiveFrameStack, queen, wear);

			setInventorySlotContents(i, usedFrame);
		}
	}
}
