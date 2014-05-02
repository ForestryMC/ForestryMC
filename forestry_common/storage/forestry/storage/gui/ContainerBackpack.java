/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.storage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.core.config.Defaults;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.utils.ItemInventory;
import forestry.storage.items.ItemBackpack;

public class ContainerBackpack extends ContainerItemInventory {

	public ContainerBackpack(EntityPlayer player, ItemInventory inventory) {
		super(inventory, player);

		int lines = 0;
		int columns = 0;
		int startX = 0;
		int startY = 0;
		if (inventory.getSizeInventory() == Defaults.SLOTS_BACKPACK_DEFAULT) {
			lines = 3;
			columns = 5;
			startX = 44;
			startY = 19;
		} else if (inventory.getSizeInventory() == Defaults.SLOTS_BACKPACK_T2) {
			lines = 5;
			columns = 9;
			startX = 8;
			startY = 8;
		}

		// Inventory
		for (int j = 0; j < lines; j++)
			for (int k = 0; k < columns; k++) {
				int slot = k + j * columns;
				addSlot(new SlotCustom(inventory, ((ItemBackpack) inventory.parent.getItem()).getValidItems(player), slot, startX + k * 18, startY + j * 18));
			}

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSecuredSlot(player.inventory, j + i * 9 + 9, 8 + j * 18, 11 + startY + lines * 18 + i * 18);
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSecuredSlot(player.inventory, i, 8 + i * 18, 11 + startY + lines * 18 + 58);
	}

	@Override
	protected boolean isAcceptedItem(EntityPlayer player, ItemStack stack) {
		// FIXME: This is incorrect.
		// However, if we return false here, even valid items will be dropped
		// while being kept inside the backpack, leading to duping.
		// inventory.parent will turn null with the current implementation if
		// something is put inside a backpack and the backpack is then
		// moved from the action bar into the player's inventory.
		if (inventory.parent == null)
			return true;

		if (!(inventory.parent.getItem() instanceof ItemBackpack))
			return false;

		return ((ItemBackpack) inventory.parent.getItem()).getDefinition().isValidItem(player, stack);
	}

}
