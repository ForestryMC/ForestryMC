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

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */

public class PacketProgressBarUpdate extends ForestryPacket implements IForestryPacketClient {

	private int windowId, dataId, value;

	public PacketProgressBarUpdate() {
	}

	public PacketProgressBarUpdate(int windowId, int dataId, int value) {
		super(PacketIdClient.GUI_PROGRESS_BAR);
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
	public void readData(DataInputStreamForestry data) throws IOException {
		windowId = data.readByte();
		dataId = data.readByte();
		value = data.readInt();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		if (player.openContainer != null && player.openContainer.windowId == windowId) {
			player.openContainer.updateProgressBar(dataId, value);
		}
	}
}
