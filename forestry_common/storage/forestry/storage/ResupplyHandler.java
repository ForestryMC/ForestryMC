/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.Event;

import net.minecraftforge.common.MinecraftForge;

import forestry.api.storage.BackpackManager;
import forestry.api.storage.BackpackResupplyEvent;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.interfaces.IResupplyHandler;
import forestry.core.utils.ItemInventory;
import forestry.storage.items.ItemBackpack;

public class ResupplyHandler implements IResupplyHandler {

	@Override
	public void resupply(EntityPlayer player) {

		// Do not attempt resupplying if this backpack is already opened.
		if (!(player.openContainer instanceof ContainerPlayer))
			return;

		for (IBackpackDefinition backpack : BackpackManager.definitions.values())
			resupply(backpack, player);

	}

	public void resupply(IBackpackDefinition backpackItem, EntityPlayer player) {

		// Get all backpacks of this type in the player's inventory
		for (ItemStack backpack : player.inventory.mainInventory) {

			if (backpack == null || backpack.stackSize <= 0)
				continue;

			if (!(backpack.getItem() instanceof ItemBackpack))
				continue;

			// Only handle those in resupply mode
			if (ItemBackpack.getMode(backpack) != BackpackMode.RESUPPLY)
				continue;

			// Delay before resupplying
			if (backpack.getItemDamage() < 40) {
				backpack.setItemDamage(backpack.getItemDamage() + 1);
				continue;
			}

			// Load their inventory
			ItemBackpack packItem = ((ItemBackpack) backpack.getItem());
			ItemInventory backpackinventory = new ItemInventory(ItemBackpack.class, packItem.getBackpackSize(), backpack);
			Event event = new BackpackResupplyEvent(player, packItem.getDefinition(), backpackinventory);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled())
				continue;

			boolean inventoryChanged = false;
			// Cycle through their contents
			for (int i = 0; i < backpackinventory.getSizeInventory(); i++) {

				ItemStack packstack = backpackinventory.getStackInSlot(i);
				if (packstack == null)
					continue;
				if (packstack.stackSize <= 0)
					continue;

				// Try to add it to the player's inventory and note any change
				boolean change = topOffPlayerInventory(player, packstack);
				if (!inventoryChanged)
					inventoryChanged = change;

				// Clear consumed stacks
				if (packstack.stackSize <= 0)
					backpackinventory.setInventorySlotContents(i, null);
			}
			// Save the backpack inventory if it changed
			if (inventoryChanged)
				backpackinventory.onGuiSaved(player);

		}
	}

	/**
	 * This tops off existing stacks in the player's inventory.
	 * 
	 * @param player
	 * @param itemstack
	 */
	private boolean topOffPlayerInventory(EntityPlayer player, ItemStack itemstack) {

		// Add to player inventory first, if there is an incomplete stack in
		// there.
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack inventoryStack = player.inventory.getStackInSlot(i);
			// We only add to existing stacks.
			if (inventoryStack == null)
				continue;

			// Already full
			if (inventoryStack.stackSize >= inventoryStack.getMaxStackSize())
				continue;

			if (inventoryStack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(inventoryStack, itemstack)) {

				inventoryStack.stackSize++;
				itemstack.stackSize--;
				if (itemstack.stackSize <= 0)
					itemstack = null;
				return true;
			}
		}
		return false;

	}

}
