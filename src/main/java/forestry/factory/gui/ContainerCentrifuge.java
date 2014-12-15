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

import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotClosed;
import forestry.factory.gadgets.MachineCentrifuge;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerCentrifuge extends ContainerForestry {

	protected final MachineCentrifuge tile;

	public ContainerCentrifuge(InventoryPlayer player, MachineCentrifuge tile) {
		super(tile);

		this.tile = tile;

		// Resource
		this.addSlot(new Slot(tile, 0, 34, 37));

		// Product Inventory
		for (int l = 0; l < 3; l++)
			for (int k = 0; k < 3; k++)
				addSlot(new SlotClosed(tile, 1 + k + l * 3, 98 + k * 18, 19 + l * 18));

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

		for (Object crafter : crafters)
			tile.sendGUINetworkData(this, (ICrafting) crafter);
	}

}
