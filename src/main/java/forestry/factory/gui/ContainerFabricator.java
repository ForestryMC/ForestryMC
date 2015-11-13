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
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.IContainerCrafting;
import forestry.core.gui.slots.SlotCraftMatrix;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.factory.inventory.InventoryFabricator;
import forestry.factory.inventory.InventoryGhostCrafting;
import forestry.factory.tiles.TileFabricator;

public class ContainerFabricator extends ContainerLiquidTanks<TileFabricator> implements IContainerCrafting {

	public ContainerFabricator(InventoryPlayer playerInventory, TileFabricator tile) {
		super(tile, playerInventory, 8, 129);

		// Internal inventory
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(tile, InventoryFabricator.SLOT_INVENTORY_1 + k + i * 9, 8 + k * 18, 84 + i * 18));
			}
		}

		// Molten resource
		this.addSlotToContainer(new SlotFiltered(tile, InventoryFabricator.SLOT_METAL, 26, 21));

		// Plan
		this.addSlotToContainer(new SlotFiltered(tile, InventoryFabricator.SLOT_PLAN, 139, 17));

		// Result
		this.addSlotToContainer(new SlotOutput(tile, InventoryFabricator.SLOT_RESULT, 139, 53));

		// Crafting matrix
		for (int l = 0; l < 3; l++) {
			for (int k = 0; k < 3; k++) {
				addSlotToContainer(new SlotCraftMatrix(this, tile.getCraftingInventory(), InventoryGhostCrafting.SLOT_CRAFTING_1 + k + l * 3, 67 + k * 18, 17 + l * 18));
			}
		}
	}

	@Override
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {

	}

	@Override
	public void updateProgressBar(int messageId, int data) {
		super.updateProgressBar(messageId, data);

		tile.getGUINetworkData(messageId, data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (Object crafter : crafters) {
			tile.sendGUINetworkData(this, (ICrafting) crafter);
		}
	}
}
