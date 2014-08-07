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
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotCrafter;
import forestry.factory.gadgets.MachineFabricator;

public class ContainerFabricator extends ContainerLiquidTanks {

	private MachineFabricator tile;

	public ContainerFabricator(InventoryPlayer player, MachineFabricator tile) {
		super(tile, tile);
		this.tile = tile;

		// Internal inventory
		for (int i = 0; i < 2; i++)
			for (int k = 0; k < 9; k++)
				addSlot(new Slot(tile, MachineFabricator.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 84 + i * 18));

		// Molten resource
		this.addSlot(new Slot(tile, MachineFabricator.SLOT_METAL, 26, 21));

		// Plan
		this.addSlot(new Slot(tile, MachineFabricator.SLOT_PLAN, 139, 17));

		// Result
		this.addSlot(new SlotCrafter(tile, tile, MachineFabricator.SLOT_RESULT, 139, 53));

		// Crafting matrix
		for (int l = 0; l < 3; l++)
			for (int k = 0; k < 3; k++)
				addSlot(new Slot(tile, MachineFabricator.SLOT_CRAFTING_1 + k + l * 3, 67 + k * 18, 17 + l * 18));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int k = 0; k < 9; k++)
				addSlot(new Slot(player, k + i * 9 + 9, 8 + k * 18, 129 + i * 18));
		// Player hotbar
		for (int j = 0; j < 9; j++)
			addSlot(new Slot(player, j, 8 + j * 18, 187));

	}

	// @Override client side only
	public void updateProgressBar(int i, int j) {
		tile.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
			tile.sendGUINetworkData(this, (ICrafting) crafters.get(i));
	}

}
