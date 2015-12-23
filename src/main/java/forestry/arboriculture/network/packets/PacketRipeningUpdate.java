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
package forestry.arboriculture.network.packets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.arboriculture.network.IRipeningPacketReceiver;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.proxy.Proxies;

public class PacketRipeningUpdate extends PacketCoordinates implements IForestryPacketClient {

	private int value;

	public PacketRipeningUpdate() {
	}

	public PacketRipeningUpdate(TileFruitPod fruitPod) {
		super(fruitPod);
		value = fruitPod.getMaturity();
	}

	public PacketRipeningUpdate(TileLeaves leaves) {
		super(leaves);
		value = leaves.getFruitColour();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.RIPENING_UPDATE;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeVarInt(value);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		value = data.readVarInt();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof IRipeningPacketReceiver) {
			((IRipeningPacketReceiver) tile).fromRipeningPacket(value);
		}
	}
}
