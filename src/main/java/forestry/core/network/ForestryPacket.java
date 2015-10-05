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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class ForestryPacket {

	protected static final String channel = "FOR";
	private final PacketId id;

	protected ForestryPacket(DataInputStreamForestry data) throws IOException {
		this.id = PacketId.INVALID;
		readData(data);
	}

	public ForestryPacket(PacketId id) {
		this.id = id;
	}

	public final FMLProxyPacket getPacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStreamForestry data = new DataOutputStreamForestry(bytes);

		try {
			data.writeByte(id.ordinal());
			writeData(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new FMLProxyPacket(new PacketBuffer(Unpooled.wrappedBuffer(bytes.toByteArray())), channel);
	}

	protected void writeData(DataOutputStreamForestry data) throws IOException {
	}

	protected void readData(DataInputStreamForestry data) throws IOException {
	}
}
