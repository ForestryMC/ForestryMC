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

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import forestry.api.storage.BackpackResupplyEvent;
import forestry.core.IResupplyHandler;
import forestry.core.inventory.ItemInventory;
import forestry.storage.inventory.ItemInventoryBackpack;
import forestry.storage.items.ItemBackpack;

public class ResupplyHandler implements IResupplyHandler {

	private static NonNullList<ItemStack> getBackpacks(PlayerInventory playerInventory) {
		NonNullList<ItemStack> backpacks = NonNullList.create();
		for (ItemStack itemStack : playerInventory.mainInventory) {
			if (itemStack.getItem() instanceof ItemBackpack) {
				backpacks.add(itemStack);
			}
		}
		return backpacks;
	}

	@Override
	public void resupply(PlayerEntity player) {

		// Do not attempt resupplying if this backpack is already opened.
		if (!(player.openContainer instanceof PlayerContainer)) {
			return;
		}

		for (ItemStack backpack : getBackpacks(player.inventory)) {
			if (ItemBackpack.getMode(backpack) == BackpackMode.RESUPPLY) {
				// Load their inventory
				ItemBackpack backpackItem = (ItemBackpack) backpack.getItem();
				ItemInventory backpackInventory = new ItemInventoryBackpack(player, backpackItem.getBackpackSize(), backpack);

				Event event = new BackpackResupplyEvent(player, backpackItem.getDefinition(), backpackInventory);
				if (!MinecraftForge.EVENT_BUS.post(event)) {
					for (int i = 0; i < backpackInventory.getSizeInventory(); i++) {
						ItemStack itemStack = backpackInventory.getStackInSlot(i);
						if (topOffPlayerInventory(player, itemStack)) {
							backpackInventory.setInventorySlotContents(i, itemStack);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * This tops off existing stacks in the player's inventory.
	 * Adds to player inventory if there is an incomplete stack in there.
	 */
	private static boolean topOffPlayerInventory(PlayerEntity player, ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}
		PlayerInventory playerInventory = player.inventory;
		List<ItemStack> inventory = new LinkedList<>();
		inventory.addAll(playerInventory.mainInventory);
		inventory.addAll(playerInventory.offHandInventory);

		for (ItemStack inventoryStack : inventory) {
			if (playerInventory.canMergeStacks(inventoryStack, itemstack)) {
				inventoryStack.grow(1);
				inventoryStack.setAnimationsToGo(5);
				itemstack.shrink(1);
				return true;
			}
		}

		return false;
	}

}
