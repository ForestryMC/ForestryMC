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

import net.minecraft.client.entity.EntityClientPlayerMP;

import cpw.mods.fml.client.FMLClientHandler;

import forestry.core.gui.ContainerTile;

public class PacketGuiEnergy extends ForestryPacket {

	private int windowId;
	private int value;

	public static void onPacketData(DataInputStreamForestry data) throws IOException {
		new PacketGuiEnergy(data);
	}

	public PacketGuiEnergy(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketGuiEnergy(int windowId, int value) {
		super(PacketId.GUI_ENERGY);
		this.windowId = windowId;
		this.value = value;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(windowId);
		data.writeVarInt(value);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		windowId = data.readVarInt();
		value = data.readVarInt();

		EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;

		if (player.openContainer instanceof ContainerTile && player.openContainer.windowId == windowId) {
			((ContainerTile) player.openContainer).onGuiEnergy(value);
		}
	}
}
