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
package forestry.mail.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketCoordinates;
import forestry.mail.tiles.TileTrader;

public class PacketTraderAddressResponse extends PacketCoordinates implements IForestryPacketClient {

	private String addressName;

	public PacketTraderAddressResponse() {
	}

	public PacketTraderAddressResponse(TileTrader tile, String addressName) {
		super(tile);
		this.addressName = addressName;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.TRADING_ADDRESS_RESPONSE;
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
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(player.worldObj);
		if ((tile instanceof TileTrader)) {
			((TileTrader) tile).handleSetAddressResponse(addressName);
		}
	}

}
