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
package forestry.greenhouse.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerSocketed;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.greenhouse.tiles.TileClimatiser;

public class ContainerClimatiser extends ContainerSocketed<TileClimatiser> {

	private ItemStack socket = ItemStack.EMPTY;

	public ContainerClimatiser(InventoryPlayer playerInventory, TileClimatiser tile) {
		super(tile, playerInventory, 8, 84);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		ItemStack socket = tile.getSocket(0);
		if (socket.isEmpty() && !this.socket.isEmpty() || !socket.isEmpty() && this.socket.isEmpty()) {
			PacketGuiUpdate packet = new PacketGuiUpdate(tile);
			sendPacketToListeners(packet);
		}
	}

}
