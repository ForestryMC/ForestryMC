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

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;
import forestry.mail.gadgets.MachineTrader;

public class PacketTraderAddress extends PacketCoordinates {

	private String addressName;

	public PacketTraderAddress(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketTraderAddress(MachineTrader tile, String addressName) {
		super(PacketId.TRADING_ADDRESS_SET, tile);

		this.addressName = addressName;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeUTF(addressName);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);

		addressName = data.readUTF();
	}

	public String getAddressName() {
		return addressName;
	}
}
