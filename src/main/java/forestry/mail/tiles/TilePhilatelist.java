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
package forestry.mail.tiles;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import forestry.api.mail.IStamps;
import forestry.api.mail.PostManager;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.tiles.TileBase;
import forestry.core.utils.InventoryUtil;
import forestry.mail.inventory.InventoryPhilatelist;

public class TilePhilatelist extends TileBase implements IInventory {
	public TilePhilatelist() {
		super(GuiId.PhilatelistGUI, "philatelist");
		setInternalInventory(new InventoryPhilatelist(this));
	}

	@Override
	public void updateServerSide() {
		if (!updateOnInterval(20)) {
			return;
		}

		ItemStack stamp = null;

		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(InventoryPhilatelist.SLOT_FILTER) == null) {
			stamp = PostManager.postRegistry.getPostOffice(worldObj).getAnyStamp(1);
		} else {
			ItemStack filter = inventory.getStackInSlot(InventoryPhilatelist.SLOT_FILTER);
			if (filter.getItem() instanceof IStamps) {
				stamp = PostManager.postRegistry.getPostOffice(worldObj).getAnyStamp(((IStamps) filter.getItem()).getPostage(filter), 1);
			}
		}

		if (stamp == null) {
			return;
		}

		// Store it.
		InventoryUtil.stowInInventory(stamp, inventory, true, InventoryPhilatelist.SLOT_BUFFER_1, InventoryPhilatelist.SLOT_BUFFER_COUNT);
	}
}
