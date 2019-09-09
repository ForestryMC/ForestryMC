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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fluids.IFluidTank;

import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.TileUtil;
import forestry.farming.ModuleFarming;
import forestry.farming.multiblock.InventoryFarm;
import forestry.farming.tiles.TileFarm;

public class ContainerFarm extends ContainerSocketed<TileFarm> {

	public static ContainerFarm fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
		TileFarm tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileFarm.class);
		return new ContainerFarm(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerFarm(int windowId, PlayerInventory playerInventory, TileFarm data) {
		super(windowId, ModuleFarming.getContainerTypes().FARM, playerInventory, data, 28, 138);

		// Resources
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new SlotFiltered(tile, InventoryFarm.SLOT_RESOURCES_1 + j + i * 2, 123 + j * 18, 22 + i * 18));
			}
		}

		// Germlings
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new SlotFiltered(tile, InventoryFarm.SLOT_GERMLINGS_1 + j + i * 2, 164 + j * 18, 22 + i * 18));
			}
		}

		// Production 1
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new SlotOutput(tile, InventoryFarm.SLOT_PRODUCTION_1 + j + i * 2, 123 + j * 18, 86 + i * 18));
			}
		}

		// Production 2
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new SlotOutput(tile, InventoryFarm.SLOT_PRODUCTION_1 + 4 + j + i * 2, 164 + j * 18, 86 + i * 18));
			}
		}

		// Fertilizer
		this.addSlot(new SlotFiltered(tile, InventoryFarm.SLOT_FERTILIZER, 63, 95));
		// Can Slot
		this.addSlot(new SlotLiquidIn(tile, InventoryFarm.SLOT_CAN, 15, 95));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToListeners(packet);
	}

	public IFluidTank getTank(int slot) {
		return tile.getMultiblockLogic().getController().getTankManager().getTank(slot);
	}

}
