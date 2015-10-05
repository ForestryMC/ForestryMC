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
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import forestry.apiculture.BeeHousingInventory;
import forestry.apiculture.gadgets.TileApiary;
import forestry.apiculture.gadgets.TileBeeHousing;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;

public class ContainerBeeHousing extends ContainerTile<TileBeeHousing> {

	public ContainerBeeHousing(InventoryPlayer player, TileBeeHousing tile, boolean hasFrames) {
		super(tile, player, 8, 108);

		// Queen/Princess
		this.addSlotToContainer(new SlotFiltered(tile, BeeHousingInventory.SLOT_QUEEN, 29, 39));

		// Drone
		this.addSlotToContainer(new SlotFiltered(tile, BeeHousingInventory.SLOT_DRONE, 29, 65));

		// Frames
		if (hasFrames) {
			final int slotFrames1 = TileApiary.ApiaryInventory.SLOT_FRAMES_1;
			this.addSlotToContainer(new SlotFiltered(tile, slotFrames1, 66, 23));
			this.addSlotToContainer(new SlotFiltered(tile, slotFrames1 + 1, 66, 52));
			this.addSlotToContainer(new SlotFiltered(tile, slotFrames1 + 2, 66, 81));
		}

		// Product Inventory
		this.addSlotToContainer(new SlotOutput(tile, 2, 116, 52));
		this.addSlotToContainer(new SlotOutput(tile, 3, 137, 39));
		this.addSlotToContainer(new SlotOutput(tile, 4, 137, 65));
		this.addSlotToContainer(new SlotOutput(tile, 5, 116, 78));
		this.addSlotToContainer(new SlotOutput(tile, 6, 95, 65));
		this.addSlotToContainer(new SlotOutput(tile, 7, 95, 39));
		this.addSlotToContainer(new SlotOutput(tile, 8, 116, 26));
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
