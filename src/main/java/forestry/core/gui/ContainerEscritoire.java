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
package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.inventory.InventoryEscritoire;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.EscritoireGame;
import forestry.core.tiles.TileEscritoire;

public class ContainerEscritoire extends ContainerTile<TileEscritoire> implements IGuiSelectable {

	private final EntityPlayer player;

	private long lastUpdate;

	public ContainerEscritoire(EntityPlayer player, TileEscritoire tile) {
		super(tile, player.inventory, 34, 153);

		this.player = player;

		// Analyze slot
		addSlotToContainer(new SlotFiltered(this.tile, InventoryEscritoire.SLOT_ANALYZE, 97, 67).setPickupWatcher(this.tile).setStackLimit(1));

		for (int i = 0; i < InventoryEscritoire.SLOTS_INPUT_COUNT; i++) {
			addSlotToContainer(new SlotFiltered(this.tile, InventoryEscritoire.SLOT_INPUT_1 + i, 17, 49 + i * 18).setBlockedTexture("slots/blocked_2"));
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotOutput(this.tile, InventoryEscritoire.SLOT_RESULTS_1 + (i * 2) + j, 177 + j * 18, 85 + i * 18));
			}
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		long gameLastUpdate = tile.getGame().getLastUpdate();
		if (lastUpdate != gameLastUpdate) {
			lastUpdate = gameLastUpdate;
			Proxies.net.sendToPlayer(new PacketGuiUpdate(tile), player);
		}
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, PacketGuiSelectRequest packet) {
		EscritoireGame.Status status = tile.getGame().getStatus();
		if (status != EscritoireGame.Status.PLAYING) {
			return;
		}

		int index = packet.getPrimaryIndex();
		if (index == -1) {
			tile.probe();
		} else {
			tile.choose(player.getGameProfile(), index);
		}
	}
}
