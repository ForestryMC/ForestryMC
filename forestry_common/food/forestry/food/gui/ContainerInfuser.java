/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.food.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.proxy.Proxies;
import forestry.food.items.ItemBeverage;
import forestry.food.items.ItemInfuser.InfuserInventory;

public class ContainerInfuser extends ContainerForestry {

	InfuserInventory inventory;

	public ContainerInfuser(InventoryPlayer inventoryplayer, InfuserInventory inventory) {
		super(inventory);
		this.inventory = inventory;

		// Input
		this.addSlot(new SlotCustom(inventory, 0, 152, 12, ForestryItem.beverage.getItemStack()));

		// Output
		this.addSlot(new SlotCustom(inventory, 1, 152, 72, ItemBeverage.class));

		// Ingredients
		this.addSlot(new Slot(inventory, 2, 12, 12));
		this.addSlot(new Slot(inventory, 3, 12, 32));
		this.addSlot(new Slot(inventory, 4, 12, 52));
		this.addSlot(new Slot(inventory, 5, 12, 72));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++)
			for (int l1 = 0; l1 < 9; l1++)
				addSlot(new Slot(inventoryplayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 103 + i1 * 18));
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++)
			addSlot(new Slot(inventoryplayer, j1, 8 + j1 * 18, 161));
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(entityplayer.worldObj))
			return;

		// Drop everything still in there.
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null)
				continue;

			Proxies.common.dropItemPlayer(entityplayer, stack);
			inventory.setInventorySlotContents(i, null);
		}

	}
}
