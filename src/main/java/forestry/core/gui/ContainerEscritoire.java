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
import net.minecraft.inventory.Slot;

import forestry.core.gadgets.TileEscritoire;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;

public class ContainerEscritoire extends ContainerForestry implements IGuiSelectable {

	final EntityPlayer player;
	final TileEscritoire tile;

	public long lastUpdate;

	public ContainerEscritoire(EntityPlayer player, TileEscritoire t) {
		super(t);

		this.player = player;
		this.tile = t;

		// Analyze slot
		addSlotToContainer(new SlotFiltered(tile, TileEscritoire.SLOT_ANALYZE, 97, 67).setCrafter(tile));

		for (int i = 0; i < TileEscritoire.SLOTS_INPUT_COUNT; i++) {
			addSlotToContainer(new SlotFiltered(tile, TileEscritoire.SLOT_INPUT_1 + i, 17, 49 + i * 18).setBlockedTexture("slots/blocked_2"));
		}

		for(int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				addSlotToContainer(new SlotOutput(tile, TileEscritoire.SLOT_RESULTS_1 + (i * 2) + j, 177 + j * 18, 85 + i * 18));
			}
		}

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 34 + j * 18, 153 + i * 18));
		// Player hotbar
		for (int k = 0; k < 9; k++)
			addSlotToContainer(new Slot(player.inventory, k, 34 + k * 18, 211));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if(lastUpdate == tile.getGame().getLastUpdate())
			return;

		lastUpdate = tile.getGame().getLastUpdate();
		tile.sendBoard(player);
	}

	public void sendTokenClick(int index) {
		PacketPayload payload = new PacketPayload(1, 0, 0);
		payload.intPayload[0] = index;
		PacketUpdate packet = new PacketUpdate(PacketIds.GUI_SELECTION_CHANGE, payload);
		Proxies.net.sendToServer(packet);
	}

	public void sendProbeClick() {
		PacketPayload payload = new PacketPayload(1, 0, 0);
		payload.intPayload[0] = -1;
		PacketUpdate packet = new PacketUpdate(PacketIds.GUI_SELECTION_CHANGE, payload);
		Proxies.net.sendToServer(packet);
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {
		if (!tile.getGame().isEnded()) {
			int index = packet.payload.intPayload[0];
			if (index == -1) {
				tile.probe();
			} else {
				tile.getGame().choose(packet.payload.intPayload[0]);
				tile.processTurnResult(player.getGameProfile());
			}
		}
	}

	@Override
	public void setSelection(PacketUpdate packet) {
	}

}
