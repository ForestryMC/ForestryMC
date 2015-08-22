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
package forestry.farming.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.farming.gadgets.TileFarmPlain;

public class ContainerFarm extends ContainerSocketed {

	private final TileFarmPlain tile;

	public ContainerFarm(InventoryPlayer playerinventory, TileFarmPlain tile) {
		super(tile, tile);

		this.tile = tile;

		// Resources
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotFiltered(tile, TileFarmPlain.SLOT_RESOURCES_1 + j + i * 2, 123 + j * 18, 22 + i * 18));
			}
		}

		// Germlings
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotFiltered(tile, TileFarmPlain.SLOT_GERMLINGS_1 + j + i * 2, 164 + j * 18, 22 + i * 18));
			}
		}

		// Production 1
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotOutput(tile, TileFarmPlain.SLOT_PRODUCTION_1 + j + i * 2, 123 + j * 18, 86 + i * 18));
			}
		}

		// Production 2
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotOutput(tile, TileFarmPlain.SLOT_PRODUCTION_1 + 4 + j + i * 2, 164 + j * 18, 86 + i * 18));
			}
		}

		// Fertilizer
		addSlotToContainer(new SlotFiltered(tile, TileFarmPlain.SLOT_FERTILIZER, 63, 95));
		// Can Slot
		addSlotToContainer(new SlotFiltered(tile, TileFarmPlain.SLOT_CAN, 15, 95));

		// Player inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerinventory, j + i * 9 + 9, 28 + j * 18, 138 + i * 18));
			}
		}
		// Player hotbar
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(playerinventory, i, 28 + i * 18, 196));
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		tile.getGUINetworkData(i, j);
		TankManager tankManager = tile.getTankManager();
		if (tankManager != null) {
			tankManager.processGuiUpdate(i, j);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		TankManager tankManager = tile.getTankManager();
		if (tankManager != null) {
			tankManager.updateGuiData(this, crafters);
		}

		for (Object crafter : crafters) {
			tile.sendGUINetworkData(this, (ICrafting) crafter);
		}
	}
	
	@Override
	public void onCraftGuiOpened(ICrafting listener) {
		super.onCraftGuiOpened(listener);
		TankManager tankManager = tile.getTankManager();
		if (tankManager != null) {
			tankManager.initGuiData(this, listener);
		}
	}

	public StandardTank getTank(int slot) {
		return tile.getTankManager().get(slot);
	}

}
