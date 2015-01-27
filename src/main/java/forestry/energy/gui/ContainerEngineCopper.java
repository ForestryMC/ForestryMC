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

import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.energy.gadgets.EngineCopper;

public class ContainerEngineCopper extends ContainerForestry {

	protected final EngineCopper engine;

	public ContainerEngineCopper(InventoryPlayer player, EngineCopper tile) {
		super(tile);

		this.engine = tile;

		this.addSlotToContainer(new SlotFiltered(tile, 0, 44, 46));

		this.addSlotToContainer(new SlotOutput(tile, 1, 98, 35));
		this.addSlotToContainer(new SlotOutput(tile, 2, 98, 53));
		this.addSlotToContainer(new SlotOutput(tile, 3, 116, 35));
		this.addSlotToContainer(new SlotOutput(tile, 4, 116, 53));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
		}

	}

	@Override
	public void updateProgressBar(int i, int j) {
		if (engine != null) {
			engine.getGUINetworkData(i, j);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (Object crafter : crafters) {
			engine.sendGUINetworkData(this, (ICrafting) crafter);
		}
	}

}
