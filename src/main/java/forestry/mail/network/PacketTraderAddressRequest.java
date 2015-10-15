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
package forestry.mail.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketIdServer;
import forestry.mail.tiles.TileTrader;

public class PacketTraderAddressRequest extends PacketCoordinates implements IForestryPacketServer {

	private String addressName;

	public PacketTraderAddressRequest() {
	}

	public PacketTraderAddressRequest(TileTrader tile, String addressName) {
		super(PacketIdServer.TRADING_ADDRESS_REQUEST, tile);
		this.addressName = addressName;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeUTF(addressName);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		addressName = data.readUTF();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		TileEntity tile = getTarget(player.worldObj);
		if ((tile instanceof TileTrader)) {
			((TileTrader) tile).handleSetAddressRequest(addressName);
		}
	}
}
