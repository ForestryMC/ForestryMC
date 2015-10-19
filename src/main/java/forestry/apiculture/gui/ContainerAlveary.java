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
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.core.gui.ContainerTile;
import forestry.core.network.PacketGuiUpdate;

public class ContainerAlveary extends ContainerTile<TileAlvearyPlain> {

	public ContainerAlveary(InventoryPlayer player, TileAlvearyPlain tile) {
		super(tile, player, 8, 108);
		ContainerBeeHelper.addSlots(this, tile, false);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToCrafters(packet);
	}
}
