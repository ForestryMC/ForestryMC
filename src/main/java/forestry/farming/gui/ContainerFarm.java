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

import net.minecraftforge.fluids.IFluidTank;

import forestry.core.fluids.ITankManager;
import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.PacketGuiUpdate;
import forestry.farming.multiblock.FarmInventory;
import forestry.farming.multiblock.TileFarm;

public class ContainerFarm extends ContainerSocketed<TileFarm> {

	public ContainerFarm(InventoryPlayer playerInventory, TileFarm tile) {
		super(tile, playerInventory, 28, 138);

		// Resources
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotFiltered(tile, FarmInventory.SLOT_RESOURCES_1 + j + i * 2, 123 + j * 18, 22 + i * 18));
			}
		}

		// Germlings
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotFiltered(tile, FarmInventory.SLOT_GERMLINGS_1 + j + i * 2, 164 + j * 18, 22 + i * 18));
			}
		}

		// Production 1
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotOutput(tile, FarmInventory.SLOT_PRODUCTION_1 + j + i * 2, 123 + j * 18, 86 + i * 18));
			}
		}

		// Production 2
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotOutput(tile, FarmInventory.SLOT_PRODUCTION_1 + 4 + j + i * 2, 164 + j * 18, 86 + i * 18));
			}
		}

		// Fertilizer
		addSlotToContainer(new SlotFiltered(tile, FarmInventory.SLOT_FERTILIZER, 63, 95));
		// Can Slot
		addSlotToContainer(new SlotFiltered(tile, FarmInventory.SLOT_CAN, 15, 95));
	}

	@Override
	public void updateProgressBar(int i, int j) {
		ITankManager tankManager = tile.getFarmController().getTankManager();
		if (tankManager != null) {
			tankManager.processGuiUpdate(i, j);
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToCrafters(packet);
	}

	public IFluidTank getTank(int slot) {
		return tile.getFarmController().getTankManager().getTank(slot);
	}

}
