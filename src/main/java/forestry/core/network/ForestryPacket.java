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

import net.minecraft.network.PacketBuffer;

import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

public abstract class ForestryPacket implements IForestryPacket {
	private final IPacketId id = getPacketId();

	@Override
	public final FMLProxyPacket getPacket() {
		ByteBufOutputStream buf = new ByteBufOutputStream(Unpooled.buffer());
		DataOutputStreamForestry data = new DataOutputStreamForestry(buf);

		try {
			data.writeByte(id.ordinal());
			writeData(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new FMLProxyPacket(new PacketBuffer(buf.buffer()), PacketHandler.channelId);
	}

	protected void writeData(DataOutputStreamForestry data) throws IOException {
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
	}
}
