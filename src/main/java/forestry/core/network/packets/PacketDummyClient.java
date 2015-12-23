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
package forestry.core.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;

public class PacketDummyClient extends PacketDummy implements IForestryPacketClient {
	public static final PacketDummyClient instance = new PacketDummyClient();

	private PacketDummyClient() {

	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.INVALID;
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {

	}
}
