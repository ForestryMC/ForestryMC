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
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.energy.gadgets.EngineTin;

public class ContainerEngineTin extends ContainerSocketed {

	protected final EngineTin tile;

	public ContainerEngineTin(InventoryPlayer player, EngineTin tile) {
		super(tile, tile);

		this.tile = tile;

		this.addSlotToContainer(new SlotFiltered(tile, EngineTin.SLOT_BATTERY, 84, 53));

		int i;
		for (i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
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

		for (Object crafter : crafters)
			tile.sendGUINetworkData(this, (ICrafting) crafter);
	}

}
