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
package forestry.apiculture.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import forestry.apiculture.gadgets.TileAlvearySieve;
import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotCustom;

public class ContainerAlvearySieve extends ContainerForestry {

	TileAlvearySieve tile;

	public ContainerAlvearySieve(InventoryPlayer player, TileAlvearySieve tile) {
		super(tile.getInternalInventory());

		this.tile = tile;
		IInventory inventory = tile.getInternalInventory();

		addSlot(new SlotCustom(inventory, 0, 94, 52, new Object[0]).setCrafter(tile));
		addSlot(new SlotCustom(inventory, 1, 115, 39, new Object[0]).setCrafter(tile));
		addSlot(new SlotCustom(inventory, 2, 73, 39, new Object[0]).setCrafter(tile));
		addSlot(new SlotCustom(inventory, 3, 94, 26, new Object[0]).setCrafter(tile));

		addSlot(new SlotCustom(inventory, TileAlvearySieve.SLOT_SIEVE, 43, 39, ForestryItem.craftingMaterial.getItemStack(1, 3)).setCrafter(tile));

		// Player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 87 + i * 18));
			}
		}
		// Player hotbar
		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(player, i, 8 + i * 18, 145));
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tile.isUseableByPlayer(entityplayer);
	}
}
