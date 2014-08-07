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
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotClosed;
import forestry.core.gui.slots.SlotLiquidContainer;
import forestry.factory.gadgets.MachineRaintank;

public class ContainerRaintank extends ContainerLiquidTanks {

	protected MachineRaintank tile;

	public ContainerRaintank(InventoryPlayer player, MachineRaintank tile) {
		super(tile, tile);

		this.tile = tile;
		this.addSlot(new SlotLiquidContainer(tile, MachineRaintank.SLOT_RESOURCE, 116, 19, true));
		this.addSlot(new SlotClosed(tile, MachineRaintank.SLOT_PRODUCT, 116, 55));

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (int i = 0; i < 9; ++i)
			this.addSlot(new Slot(player, i, 8 + i * 18, 142));

	}

	// @Override client side only
	public void updateProgressBar(int i, int j) {
		tile.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
			tile.sendGUINetworkData(this, (ICrafting) crafters.get(i));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tile.isUseableByPlayer(entityplayer);
	}
}
