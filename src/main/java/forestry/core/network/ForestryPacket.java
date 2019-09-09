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

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.network.PacketBuffer;

import io.netty.buffer.Unpooled;

public abstract class ForestryPacket implements IForestryPacket {
	@Override
	public final Pair<PacketBuffer, Integer> getPacketData() {
		PacketBufferForestry data = new PacketBufferForestry(Unpooled.buffer());

		IPacketId id = getPacketId();
		int ordinal = id.ordinal();
		data.writeByte(id.ordinal());
		writeData(data);

		return Pair.of(data, ordinal);
	}

	protected abstract void writeData(PacketBufferForestry data);
}
