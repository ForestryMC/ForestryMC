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
import net.minecraft.tileentity.TileEntity;

import forestry.core.proxy.Proxies;

public class PacketTileStream extends PacketCoordinates implements IForestryPacketClient {

	private IStreamable streamable;

	public PacketTileStream() {
	}

	public <T extends TileEntity & IStreamable> PacketTileStream(T streamable) {
		super(PacketIdClient.TILE_FORESTRY_UPDATE, streamable);
		this.streamable = streamable;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		streamable.writeData(data);
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IStreamable) {
			((IStreamable) tile).readData(data);
		}
	}
}
