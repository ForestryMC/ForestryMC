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

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.fluids.IFluidTank;

import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.TileUtil;
import forestry.farming.features.FarmingContainers;
import forestry.farming.multiblock.InventoryFarm;
import forestry.farming.tiles.TileFarm;

public class ContainerFarm extends ContainerSocketed<TileFarm> {

	public static ContainerFarm fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
		TileFarm tile = TileUtil.getTile(inv.player.level, data.readBlockPos(), TileFarm.class);
		return new ContainerFarm(windowId, inv, tile);
	}

	public ContainerFarm(int windowId, Inventory playerInventory, TileFarm data) {
		super(windowId, FarmingContainers.FARM.containerType(), playerInventory, data, 28, 138);

		// Resources
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new SlotFiltered(tile, InventoryFarm.CONFIG.resourcesStart + j + i * 2, 123 + j * 18, 22 + i * 18));
			}
		}

		// Germlings
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new SlotFiltered(tile, InventoryFarm.CONFIG.germlingsStart + j + i * 2, 164 + j * 18, 22 + i * 18));
			}
		}

		// Production 1
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new SlotOutput(tile, InventoryFarm.CONFIG.productionStart + j + i * 2, 123 + j * 18, 86 + i * 18));
			}
		}

		// Production 2
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new SlotOutput(tile, InventoryFarm.CONFIG.productionStart + 4 + j + i * 2, 164 + j * 18, 86 + i * 18));
			}
		}

		// Fertilizer
		this.addSlot(new SlotFiltered(tile, InventoryFarm.CONFIG.fertilizerStart, 63, 95));
		// Can Slot
		this.addSlot(new SlotLiquidIn(tile, InventoryFarm.CONFIG.canStart, 15, 95));
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToListeners(packet);
	}

	public IFluidTank getTank(int slot) {
		return tile.getMultiblockLogic().getController().getTankManager().getTank(slot);
	}

}
