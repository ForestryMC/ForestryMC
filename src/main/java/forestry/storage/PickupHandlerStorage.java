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
package forestry.storage;

import forestry.api.storage.IBackpackDefinition;
import forestry.core.IPickupHandler;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.gui.ContainerNaturalistBackpack;
import forestry.storage.items.ItemBackpack;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PickupHandlerStorage implements IPickupHandler {

	@Override
	public boolean onItemPickup(EntityPlayer player, EntityItem entityitem) {

		ItemStack itemstack = entityitem.getEntityItem();
		if (itemstack.isEmpty()) {
			return false;
		}

		// Do not pick up if a backpack is open
		if (player.openContainer instanceof ContainerBackpack || player.openContainer instanceof ContainerNaturalistBackpack) {
			return false;
		}

		// Make sure to top off manually placed itemstacks in player inventory first
		topOffPlayerInventory(player, itemstack);

		for (ItemStack pack : player.inventory.mainInventory) {
			if (pack.isEmpty() || itemstack.isEmpty()) {
				break;
			}

			if (!(pack.getItem() instanceof ItemBackpack)) {
				continue;
			}

			ItemBackpack backpack = (ItemBackpack) pack.getItem();
			IBackpackDefinition backpackDefinition = backpack.getDefinition();
			if (backpackDefinition.getFilter().test(itemstack)) {
				ItemBackpack.tryStowing(player, pack, itemstack);
			}
		}

		return itemstack.isEmpty();
	}

	/**
	 * This tops off existing stacks in the player's inventory. That way you can keep f.e. a stack of dirt or cobblestone in your inventory which gets refreshed
	 * constantly by picked up items.
	 */
	private static void topOffPlayerInventory(EntityPlayer player, ItemStack itemstack) {

		// Add to player inventory first, if there is an incomplete stack in
		// there.
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack inventoryStack = player.inventory.getStackInSlot(i);
			// We only add to existing stacks.
			if (inventoryStack.isEmpty()) {
				continue;
			}

			// Already full
			if (inventoryStack.getCount() >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			if (inventoryStack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(inventoryStack, itemstack)) {
				int space = inventoryStack.getMaxStackSize() - inventoryStack.getCount();

				if (space > itemstack.getCount()) {
					// Enough space to add all
					inventoryStack.grow(itemstack.getCount());
					itemstack.setCount(0);
				} else {
					// Only part can be added
					inventoryStack.setCount(inventoryStack.getMaxStackSize());
					itemstack.shrink(space);
				}
			}
		}

	}

}
