/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
