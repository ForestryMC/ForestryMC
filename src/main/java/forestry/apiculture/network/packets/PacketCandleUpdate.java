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
package forestry.apiculture.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.apiculture.tiles.TileCandle;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.proxy.Proxies;

public class PacketCandleUpdate extends PacketCoordinates implements IForestryPacketClient {

	private int colour;
	private boolean lit;

	public PacketCandleUpdate() {
	}

	public PacketCandleUpdate(TileCandle tileCandle) {
		super(tileCandle);

		colour = tileCandle.getColour();
		lit = tileCandle.isLit();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.CANDLE_UPDATE;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeInt(colour);
		data.writeBoolean(lit);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		colour = data.readInt();
		lit = data.readBoolean();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tileEntity = getTarget(Proxies.common.getRenderWorld());
		if (tileEntity instanceof TileCandle) {
			((TileCandle) tileEntity).onPacketUpdate(colour, lit);
		}
	}
}
