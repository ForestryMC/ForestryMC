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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotCraftAuto;
import forestry.core.gui.slots.SlotWorking;
import forestry.core.interfaces.IContainerCrafting;
import forestry.factory.gadgets.MachineMoistener;

public class ContainerMoistener extends ContainerLiquidTanks implements IContainerCrafting {

	protected MachineMoistener tile;

	public ContainerMoistener(InventoryPlayer player, MachineMoistener tile) {
		super(tile, tile);

		this.tile = tile;
		// Stash
		for (int l = 0; l < 2; l++)
			for (int k1 = 0; k1 < 3; k1++)
				addSlot(new Slot(tile, k1 + l * 3, 39 + k1 * 18, 16 + l * 18));
		// Reservoir
		for (int k1 = 0; k1 < 3; k1++)
			addSlot(new Slot(tile, k1 + 6, 39 + k1 * 18, 22 + 36));

		// Working slot
		this.addSlot(new SlotWorking(tile, 9, 105, 37));

		// Product slot
		this.addSlot(new Slot(tile, 10, 143, 55));
		// Boxes
		this.addSlot(new SlotCraftAuto(this, tile, 11, 143, 19));

		// Player inventory
		int var3;
		for (var3 = 0; var3 < 3; ++var3)
			for (int var4 = 0; var4 < 9; ++var4)
				this.addSlot(new Slot(player, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));

		for (var3 = 0; var3 < 9; ++var3)
			this.addSlot(new Slot(player, var3, 8 + var3 * 18, 142));

	}

	@Override
	public void onCraftMatrixChanged(IInventory iinventory, int slot) {
		inventory.setInventorySlotContents(slot, iinventory.getStackInSlot(slot));
		tile.checkRecipe();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tile.isUseableByPlayer(player);
	}

}
