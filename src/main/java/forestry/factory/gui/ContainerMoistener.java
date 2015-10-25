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
import net.minecraft.inventory.IInventory;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotCraftAuto;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotWorking;
import forestry.core.interfaces.IContainerCrafting;
import forestry.factory.gadgets.MachineMoistener;

public class ContainerMoistener extends ContainerLiquidTanks<MachineMoistener> implements IContainerCrafting {

	public ContainerMoistener(InventoryPlayer player, MachineMoistener tile) {
		super(tile, player, 8, 84);

		// Stash
		for (int l = 0; l < 2; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlotToContainer(new SlotFiltered(tile, k1 + l * 3, 39 + k1 * 18, 16 + l * 18));
			}
		}
		// Reservoir
		for (int k1 = 0; k1 < 3; k1++) {
			addSlotToContainer(new SlotFiltered(tile, k1 + 6, 39 + k1 * 18, 22 + 36));
		}

		// Working slot
		this.addSlotToContainer(new SlotWorking(tile, 9, 105, 37));

		// Product slot
		this.addSlotToContainer(new SlotFiltered(tile, 10, 143, 55));
		// Boxes
		this.addSlotToContainer(new SlotCraftAuto(this, tile, 11, 143, 19));
	}

	@Override
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {
		tile.setInventorySlotContents(slot, iinventory.getStackInSlot(slot));
		tile.checkRecipe();
	}

}
