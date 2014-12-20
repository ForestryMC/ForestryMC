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

import forestry.apiculture.gadgets.TileAlvearyPlain;
import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotClosed;
import forestry.core.gui.slots.SlotCustom;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerAlveary extends ContainerForestry {

	private final TileAlvearyPlain tile;

	public ContainerAlveary(InventoryPlayer player, TileAlvearyPlain tile) {
		super(tile);

		this.tile = tile;
		tile.sendNetworkUpdate();

		// Queen/Princess
		this.addSlotToContainer(new SlotCustom(tile, TileAlvearyPlain.SLOT_QUEEN, 29, 39, ForestryItem.beePrincessGE, ForestryItem.beeQueenGE));

		// Drone
		this.addSlotToContainer(new SlotCustom(tile, TileAlvearyPlain.SLOT_DRONE, 29, 65, ForestryItem.beeDroneGE));

		// Product Inventory
		this.addSlotToContainer(new SlotClosed(tile, 2, 116, 52));
		this.addSlotToContainer(new SlotClosed(tile, 3, 137, 39));
		this.addSlotToContainer(new SlotClosed(tile, 4, 137, 65));
		this.addSlotToContainer(new SlotClosed(tile, 5, 116, 78));
		this.addSlotToContainer(new SlotClosed(tile, 6, 95, 65));
		this.addSlotToContainer(new SlotClosed(tile, 7, 95, 39));
		this.addSlotToContainer(new SlotClosed(tile, 8, 116, 26));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 108 + i * 18));
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(player, i, 8 + i * 18, 166));
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
