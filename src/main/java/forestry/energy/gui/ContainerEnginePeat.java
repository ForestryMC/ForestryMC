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
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.energy.tiles.TileEnginePeat;

public class ContainerEnginePeat extends ContainerTile<TileEnginePeat> {

	public ContainerEnginePeat(InventoryPlayer player, TileEnginePeat tile) {
		super(tile, player, 8, 84);

		this.addSlotToContainer(new SlotFiltered(tile, 0, 44, 46));

		this.addSlotToContainer(new SlotOutput(tile, 1, 98, 35));
		this.addSlotToContainer(new SlotOutput(tile, 2, 98, 53));
		this.addSlotToContainer(new SlotOutput(tile, 3, 116, 35));
		this.addSlotToContainer(new SlotOutput(tile, 4, 116, 53));
	}

	@Override
	public void updateProgressBar(int i, int j) {
		if (tile != null) {
			tile.getGUINetworkData(i, j);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (Object crafter : crafters) {
			tile.sendGUINetworkData(this, (ICrafting) crafter);
		}
	}
}
