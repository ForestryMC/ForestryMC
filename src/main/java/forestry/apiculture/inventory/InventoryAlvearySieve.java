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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.watchers.ISlotPickupWatcher;
import forestry.core.utils.ItemStackUtil;
import forestry.plugins.PluginCore;

public class InventoryAlvearySieve extends InventoryAdapterTile<TileAlvearySieve> implements ISlotPickupWatcher {
	public static final int SLOT_POLLEN_1 = 0;
	public static final int SLOTS_POLLEN_COUNT = 4;
	public static final int SLOT_SIEVE = 4;

	public InventoryAlvearySieve(TileAlvearySieve alvearySieve) {
		super(alvearySieve, 5, "Items", 1);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return ItemStackUtil.isIdenticalItem(PluginCore.items.craftingMaterial.getWovenSilk(), itemStack);
	}

	public boolean canStorePollen() {
		if (getStackInSlot(SLOT_SIEVE) == null) {
			return false;
		}

		for (int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
			if (getStackInSlot(i) == null) {
				return true;
			}
		}

		return false;
	}

	public void storePollenStack(ItemStack itemstack) {
		for (int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
			if (getStackInSlot(i) == null) {
				setInventorySlotContents(i, itemstack);
				return;
			}
		}
	}

	/* ISlotPickupWatcher */
	@Override
	public void onPickupFromSlot(int slotIndex, EntityPlayer player) {
		if (slotIndex == SLOT_SIEVE) {
			for (int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
				setInventorySlotContents(i, null);
			}
		} else {
			setInventorySlotContents(SLOT_SIEVE, null);
		}
	}
}
