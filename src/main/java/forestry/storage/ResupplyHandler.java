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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;

import forestry.api.storage.BackpackResupplyEvent;
import forestry.core.interfaces.IResupplyHandler;
import forestry.core.inventory.ItemInventory;
import forestry.core.inventory.ItemInventoryBackpack;
import forestry.storage.items.ItemBackpack;

public class ResupplyHandler implements IResupplyHandler {

	private static List<ItemStack> backpacks(InventoryPlayer playerInventory) {
		List<ItemStack> backpacks = new ArrayList<ItemStack>();
		for (ItemStack itemStack : playerInventory.mainInventory) {
			if (itemStack != null && itemStack.stackSize > 0 && (itemStack.getItem() instanceof ItemBackpack)) {
				backpacks.add(itemStack);
			}
		}
		return backpacks;
	}

	/**
	 * This tops off existing stacks in the player's inventory.
	 */
	private static boolean topOffPlayerInventory(EntityPlayer player, ItemStack itemstack) {

		// Add to player inventory first, if there is an incomplete stack in
		// there.
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack inventoryStack = player.inventory.getStackInSlot(i);
			// We only add to existing stacks.
			if (inventoryStack == null) {
				continue;
			}

			// Already full
			if (inventoryStack.stackSize >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			if (inventoryStack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(inventoryStack, itemstack)) {
				inventoryStack.stackSize++;
				itemstack.stackSize--;
				return true;
			}
		}
		return false;

	}

	@Override
	public void resupply(EntityPlayer player) {

		// Do not attempt resupplying if this backpack is already opened.
		if (!(player.openContainer instanceof ContainerPlayer)) {
			return;
		}

		for (ItemStack backpack : backpacks(player.inventory)) {

			BackpackMode mode = ItemBackpack.getMode(backpack);

			// Only handle those in resupply mode
			if (!(mode == BackpackMode.RESUPPLY || mode == BackpackMode.RESUPPLYLOCKED)) {
				continue;
			}

			// Delay before resupplying
			// new delay code - saves into NBT otherwise constant metadata change = silly animation
			if (ItemBackpack.getDelayTime(backpack) < 40) {
				ItemBackpack.increaseDelayTime(backpack);
				continue;
			}

			// reset the delay
			ItemBackpack.resetDelayTime(backpack);

			// Load their inventory
			ItemBackpack backpackItem = ((ItemBackpack) backpack.getItem());
			ItemInventory backpackInventory = new ItemInventoryBackpack(player, backpackItem.getBackpackSize(), backpack);

			Event event = new BackpackResupplyEvent(player, backpackItem.getDefinition(), backpackInventory);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled()) {
				continue;
			}

			// Cycle through their contents
			for (int i = 0; i < backpackInventory.getSizeInventory(); i++) {

				ItemStack itemStack = backpackInventory.getStackInSlot(i);
				if (itemStack == null || itemStack.stackSize <= 0) {
					continue;
				}

				if (itemStack.stackSize == 1 && mode == BackpackMode.RESUPPLYLOCKED) {
					continue;
				}

				// Try to add it to the player's inventory and note any change
				boolean change = topOffPlayerInventory(player, itemStack);

				if (change) {
					backpackInventory.setInventorySlotContents(i, itemStack);
				}
			}
		}
	}

}
