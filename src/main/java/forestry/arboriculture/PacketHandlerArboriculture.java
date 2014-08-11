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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.arboriculture.gadgets.TileLeaves;
import forestry.arboriculture.network.PacketLeafUpdate;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.network.ILocatedPacket;
import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;

public class PacketHandlerArboriculture implements IPacketHandler {

	@Override
	public void onPacketData(int packetID, DataInputStream data, EntityPlayer player) {
		try {

			switch (packetID) {
			case PacketIds.LEAF_UPDATE:
				PacketLeafUpdate packet = new PacketLeafUpdate();
				packet.readData(data);
				onLeafUpdate(packet);
				break;
			}

		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void onLeafUpdate(PacketLeafUpdate packet) {

		TileEntity tile = ((ILocatedPacket) packet).getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileLeaves)
			((TileLeaves) tile).fromPacket(packet);

	}


}
