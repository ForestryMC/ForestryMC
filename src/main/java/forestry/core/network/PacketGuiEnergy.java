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
package forestry.core.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import forestry.core.gui.ContainerTile;

public class PacketGuiEnergy extends ForestryPacket implements IForestryPacketClient {

	private int windowId;
	private int value;

	public PacketGuiEnergy() {
	}

	public PacketGuiEnergy(int windowId, int value) {
		super(PacketIdClient.GUI_ENERGY);
		this.windowId = windowId;
		this.value = value;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(windowId);
		data.writeVarInt(value);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		windowId = data.readVarInt();
		value = data.readVarInt();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		if (player.openContainer instanceof ContainerTile && player.openContainer.windowId == windowId) {
			((ContainerTile) player.openContainer).onGuiEnergy(value);
		}
	}
}
