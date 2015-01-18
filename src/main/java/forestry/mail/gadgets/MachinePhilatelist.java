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
package forestry.mail.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.mail.IStamps;
import forestry.api.mail.PostManager;
import forestry.core.gadgets.TileBase;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.utils.StackUtils;

public class MachinePhilatelist extends TileBase implements IInventory {

	// / CONSTANTS
	public static final short SLOT_FILTER = 0;
	public static final short SLOT_BUFFER_1 = 1;
	public static final short SLOT_BUFFER_COUNT = 27;

	public MachinePhilatelist() {
		setInternalInventory(new TileInventoryAdapter(this, 28, "INV") {
			@Override
			public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
				return itemStack.getItem() instanceof IStamps;
			}
		});
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.PhilatelistGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	// / UPDATING
	@Override
	public void updateServerSide() {
		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		ItemStack stamp = null;

		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(SLOT_FILTER) == null)
			stamp = PostManager.postRegistry.getPostOffice(worldObj).getAnyStamp(1);
		else {
			ItemStack filter = inventory.getStackInSlot(SLOT_FILTER);
			if (filter.getItem() instanceof IStamps)
				stamp = PostManager.postRegistry.getPostOffice(worldObj).getAnyStamp(((IStamps) filter.getItem()).getPostage(filter), 1);
		}

		if (stamp == null)
			return;

		// Store it.
		StackUtils.stowInInventory(stamp, inventory, true, SLOT_BUFFER_1, SLOT_BUFFER_COUNT);
	}
}
