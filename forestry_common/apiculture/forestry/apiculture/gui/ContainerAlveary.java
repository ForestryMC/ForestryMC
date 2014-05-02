/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

import forestry.apiculture.gadgets.TileAlvearyPlain;
import forestry.core.config.ForestryItem;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotClosed;
import forestry.core.gui.slots.SlotCustom;

public class ContainerAlveary extends ContainerForestry {

	private TileAlvearyPlain tile;

	public ContainerAlveary(InventoryPlayer player, TileAlvearyPlain tile) {
		super(tile);

		this.tile = tile;
		tile.sendNetworkUpdate();

		// Queen/Princess
		this.addSlot(new SlotCustom(tile, TileAlvearyPlain.SLOT_QUEEN, 29, 39, new Object[] { ForestryItem.beePrincessGE, ForestryItem.beeQueenGE }));

		// Drone
		this.addSlot(new SlotCustom(tile, TileAlvearyPlain.SLOT_DRONE, 29, 65, new Object[] { ForestryItem.beeDroneGE }));

		// Product Inventory
		this.addSlot(new SlotClosed(tile, 2, 116, 52));
		this.addSlot(new SlotClosed(tile, 3, 137, 39));
		this.addSlot(new SlotClosed(tile, 4, 137, 65));
		this.addSlot(new SlotClosed(tile, 5, 116, 78));
		this.addSlot(new SlotClosed(tile, 6, 95, 65));
		this.addSlot(new SlotClosed(tile, 7, 95, 39));
		this.addSlot(new SlotClosed(tile, 8, 116, 26));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 108 + i * 18));
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(player, i, 8 + i * 18, 166));
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
