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
import forestry.core.gui.slots.SlotClosed;
import forestry.core.gui.slots.SlotLiquidContainer;
import forestry.factory.gadgets.MachineSqueezer;

public class ContainerSqueezer extends ContainerLiquidTanks {

	protected MachineSqueezer tile;

	public ContainerSqueezer(InventoryPlayer player, MachineSqueezer tile) {
		super(tile, tile);

		this.tile = tile;

		// Resource inventory
		for (int l = 0; l < 3; l++)
			for (int k = 0; k < 3; k++)
				addSlot(new Slot(tile, k + l * 3, 19 + k * 18, 18 + l * 18));

		// Remnants slot
		this.addSlot(new SlotClosed(tile, 9, 123, 19));

		// Can slot
		this.addSlot(new SlotLiquidContainer(tile, 10, 106, 55, true));
		// Output slot
		this.addSlot(new SlotClosed(tile, 11, 140, 55));

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++)
			for (int l1 = 0; l1 < 9; l1++)
				addSlot(new Slot(player, l1 + i1 * 9 + 9, 8 + l1 * 18, 84 + i1 * 18));
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++)
			addSlot(new Slot(player, j1, 8 + j1 * 18, 142));
	}

	@Override
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
