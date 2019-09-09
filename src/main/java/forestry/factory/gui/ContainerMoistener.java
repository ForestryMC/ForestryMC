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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotWatched;
import forestry.core.gui.slots.SlotWorking;
import forestry.core.inventory.watchers.ISlotChangeWatcher;
import forestry.core.tiles.TileUtil;
import forestry.factory.ModuleFactory;
import forestry.factory.tiles.TileMoistener;

public class ContainerMoistener extends ContainerLiquidTanks<TileMoistener> implements ISlotChangeWatcher {

	public static ContainerMoistener fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
		TileMoistener tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileMoistener.class);
		return new ContainerMoistener(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerMoistener(int windowId, PlayerInventory player, TileMoistener tile) {
		super(windowId, ModuleFactory.getContainerTypes().MOISTENER, player, tile, 8, 84);

		// Stash
		for (int l = 0; l < 2; l++) {
			for (int k1 = 0; k1 < 3; k1++) {
				addSlot(new SlotFiltered(this.tile, k1 + l * 3, 39 + k1 * 18, 16 + l * 18));
			}
		}
		// Reservoir
		for (int k1 = 0; k1 < 3; k1++) {
			addSlot(new SlotFiltered(this.tile, k1 + 6, 39 + k1 * 18, 22 + 36));
		}

		// Working slot
		this.addSlot(new SlotWorking(this.tile, 9, 105, 37));

		// Product slot
		this.addSlot(new SlotFiltered(this.tile, 10, 143, 55));
		// Boxes
		this.addSlot(new SlotWatched(this.tile, 11, 143, 19).setChangeWatcher(this));
	}

	@Override
	public void onSlotChanged(IInventory inventory, int slot) {
		tile.setInventorySlotContents(slot, inventory.getStackInSlot(slot));
		tile.checkRecipe();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void updateProgressBar(int messageId, int data) {
		super.updateProgressBar(messageId, data);

		tile.getGUINetworkData(messageId, data);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (IContainerListener crafter : listeners) {
			tile.sendGUINetworkData(this, crafter);
		}
	}
}
