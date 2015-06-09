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

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */

public class PacketGuiInteger extends ForestryPacket {

	private int windowId, dataId, value;

	public PacketGuiInteger(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketGuiInteger(int windowId, int dataId, int value) {
		super(PacketId.GUI_INTEGER);
		this.windowId = windowId;
		this.dataId = dataId;
		this.value = value;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeByte(windowId);
		data.writeByte(dataId);
		data.writeInt(value);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		windowId = data.readByte();
		dataId = data.readByte();
		value = data.readInt();

		EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;

		if (player.openContainer != null && player.openContainer.windowId == windowId) {
			player.openContainer.updateProgressBar(dataId, value);
		}
	}
}
