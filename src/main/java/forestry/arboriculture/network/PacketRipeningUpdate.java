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
package forestry.arboriculture.network;

import java.io.IOException;

import net.minecraft.tileentity.TileEntity;

import forestry.arboriculture.gadgets.TileFruitPod;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;

public class PacketRipeningUpdate extends PacketCoordinates {

	private int value;

	public static void onPacketData(DataInputStreamForestry data) throws IOException {
		new PacketRipeningUpdate(data);
	}

	private PacketRipeningUpdate(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketRipeningUpdate(TileFruitPod fruitPod) {
		super(PacketId.RIPENING_UPDATE, fruitPod);
		value = fruitPod.getMaturity();
	}

	public PacketRipeningUpdate(TileLeaves leaves) {
		super(PacketId.RIPENING_UPDATE, leaves);
		value = leaves.getFruitColour();
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

		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof TileLeaves) {
			((TileLeaves) tile).fromRipeningPacket(value);
		} else if (tile instanceof TileFruitPod) {
			((TileFruitPod) tile).fromRipeningPacket(value);
		}
	}

	public int getValue() {
		return value;
	}
}
