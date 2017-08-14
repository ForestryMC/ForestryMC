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

import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import forestry.core.utils.Log;

import io.netty.buffer.Unpooled;

public abstract class ForestryPacket implements IForestryPacket {
	@Override
	public final FMLProxyPacket getPacket() {
		PacketBufferForestry data = new PacketBufferForestry(Unpooled.buffer());

		try {
			IPacketId id = getPacketId();
			data.writeByte(id.ordinal());
			writeData(data);
		} catch (IOException e) {
			Log.error("Failed to write packet.", e);
		}

		return new FMLProxyPacket(data, PacketHandler.channelId);
	}

	protected abstract void writeData(PacketBufferForestry data) throws IOException;
}
