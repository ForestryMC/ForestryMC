/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerSocketed;
import forestry.energy.gadgets.EngineTin;

public class ContainerEngineTin extends ContainerSocketed {

	protected EngineTin tile;

	public ContainerEngineTin(InventoryPlayer player, EngineTin tile) {
		super(tile, tile);

		this.tile = tile;
		this.addSlot(new Slot(tile, 0, 84, 53));

		int i;
		for (i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (i = 0; i < 9; ++i)
			this.addSlot(new Slot(player, i, 8 + i * 18, 142));
	}

	@Override
	public void updateProgressBar(int i, int j) {
		if (tile != null)
			tile.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (tile == null)
			return;

		for (int i = 0; i < crafters.size(); i++)
			tile.sendGUINetworkData(this, (ICrafting) crafters.get(i));
	}

}
