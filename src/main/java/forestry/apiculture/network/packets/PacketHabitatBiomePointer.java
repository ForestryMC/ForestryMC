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
import net.minecraft.util.ChunkCoordinates;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.proxy.Proxies;

public class PacketHabitatBiomePointer extends PacketCoordinates implements IForestryPacketClient {

	public PacketHabitatBiomePointer() {
	}

	public PacketHabitatBiomePointer(ChunkCoordinates coordinates) {
		super(coordinates);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.HABITAT_BIOME_POINTER;
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		Proxies.render.setHabitatLocatorTexture(player, getCoordinates());
	}
}
