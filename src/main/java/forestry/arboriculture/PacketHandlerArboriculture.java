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
package forestry.arboriculture;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.arboriculture.gadgets.TileLeaves;
import forestry.arboriculture.network.PacketRipeningUpdate;
import forestry.core.network.IPacketHandler;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;

public class PacketHandlerArboriculture implements IPacketHandler {

	@Override
	public boolean onPacketData(PacketId packetID, DataInputStream data, EntityPlayer player) throws IOException {

		switch (packetID) {
			case RIPENING_UPDATE: {
				PacketRipeningUpdate packet = new PacketRipeningUpdate(data);
				onRipeningUpdate(packet);
				return true;
			}
		}

		return false;
	}

	private static void onRipeningUpdate(PacketRipeningUpdate packet) {
		TileEntity tile = packet.getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileLeaves) {
			((TileLeaves) tile).fromRipeningPacket(packet);
		}
	}
}
