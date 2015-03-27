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
import forestry.arboriculture.gadgets.TileSapling;
import forestry.arboriculture.network.PacketLeaf;
import forestry.arboriculture.network.PacketRipeningUpdate;
import forestry.arboriculture.network.PacketSapling;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.network.PacketIds;
import forestry.core.proxy.Proxies;

public class PacketHandlerArboriculture implements IPacketHandler {

	@Override
	public boolean onPacketData(int packetID, DataInputStream data, EntityPlayer player) throws IOException {

		switch (packetID) {
			case PacketIds.SAPLING: {
				PacketSapling packet = new PacketSapling();
				packet.readData(data);
				onSaplingPacket(packet);
				return true;
			}
			case PacketIds.LEAF: {
				PacketLeaf packet = new PacketLeaf();
				packet.readData(data);
				onLeafPacket(packet);
				return true;
			}
			case PacketIds.RIPENING_UPDATE: {
				PacketRipeningUpdate packet = new PacketRipeningUpdate();
				packet.readData(data);
				onRipeningUpdate(packet);
				return true;
			}
		}

		return false;
	}

	private void onSaplingPacket(PacketSapling packet) {
		TileEntity tile = packet.getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileSapling) {
			((TileSapling) tile).fromPacket(packet);
		}
	}

	private void onLeafPacket(PacketLeaf packet) {

		TileEntity tile = packet.getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileLeaves) {
			((TileLeaves) tile).fromPacket(packet);
		}

	}

	private void onRipeningUpdate(PacketRipeningUpdate packet) {

		TileEntity tile = packet.getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileLeaves) {
			((TileLeaves) tile).fromRipeningPacket(packet);
		}

	}

}
