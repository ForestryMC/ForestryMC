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

import forestry.api.storage.BackpackResupplyEvent;
import forestry.core.IResupplyHandler;
import forestry.core.inventory.ItemInventory;
import forestry.storage.inventory.ItemInventoryBackpack;
import forestry.storage.items.ItemBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ResupplyHandler implements IResupplyHandler {

	private static NonNullList<ItemStack> getBackpacks(InventoryPlayer playerInventory) {
		NonNullList<ItemStack> backpacks = NonNullList.create();
		for (ItemStack itemStack : playerInventory.mainInventory) {
			if (itemStack.getItem() instanceof ItemBackpack) {
				backpacks.add(itemStack);
			}
		}
		return backpacks;
	}

	@Override
	public void resupply(EntityPlayer player) {

		// Do not attempt resupplying if this backpack is already opened.
		if (!(player.openContainer instanceof ContainerPlayer)) {
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
	private static boolean topOffPlayerInventory(EntityPlayer player, ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}
		InventoryPlayer playerInventory = player.inventory;
		NonNullList<ItemStack> mainInventory = playerInventory.mainInventory;

		for (ItemStack inventoryStack : mainInventory) {
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
