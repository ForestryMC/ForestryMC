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
package forestry.mail.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.tiles.TileUtil;
import forestry.mail.ModuleMail;
import forestry.mail.inventory.InventoryStampCollector;
import forestry.mail.tiles.TileStampCollector;

public class ContainerStampCollector extends ContainerTile<TileStampCollector> {

	public static ContainerStampCollector fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
		TileStampCollector tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileStampCollector.class);
		return new ContainerStampCollector(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerStampCollector(int windowId, PlayerInventory inv, TileStampCollector tile) {
		super(windowId, ModuleMail.getContainerTypes().STAMP_COLLECTOR, inv, tile, 8, 111);

		// Filter
		addSlot(new SlotFiltered(tile, InventoryStampCollector.SLOT_FILTER, 80, 19));

		// Collected Stamps
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new SlotOutput(tile, j + i * 9 + InventoryStampCollector.SLOT_BUFFER_1, 8 + j * 18, 46 + i * 18));
			}
		}
	}
}
