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

import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.factory.gadgets.MachineCentrifuge;

public class ContainerCentrifuge extends ContainerTile<MachineCentrifuge> {

	public ContainerCentrifuge(InventoryPlayer player, MachineCentrifuge tile) {
		super(tile, player, 8, 84);

		// Resource
		this.addSlotToContainer(new SlotFiltered(tile, 0, 34, 37));

		// Product Inventory
		for (int l = 0; l < 3; l++) {
			for (int k = 0; k < 3; k++) {
				addSlotToContainer(new SlotOutput(tile, 1 + k + l * 3, 98 + k * 18, 19 + l * 18));
			}
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		tile.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (Object crafter : crafters) {
			tile.sendGUINetworkData(this, (ICrafting) crafter);
		}
	}

}
