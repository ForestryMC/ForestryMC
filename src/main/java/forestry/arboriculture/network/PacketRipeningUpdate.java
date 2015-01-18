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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.arboriculture.gadgets.TileLeaves;
import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketIds;

public class PacketRipeningUpdate extends ForestryPacket {

	public int colourFruits;
	private int posX, posY, posZ;

	public PacketRipeningUpdate() {
	}

	public PacketRipeningUpdate(TileLeaves leaves, int fruitColour) {
		super(PacketIds.RIPENING_UPDATE);
		posX = leaves.xCoord;
		posY = leaves.yCoord;
		posZ = leaves.zCoord;
		colourFruits = fruitColour;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);

		data.writeInt(posX);
		data.writeInt(posY);
		data.writeInt(posZ);
		data.writeInt(colourFruits);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);

		posX = data.readInt();
		posY = data.readInt();
		posZ = data.readInt();
		colourFruits = data.readInt();
	}

	public TileEntity getTarget(World world) {
		return world.getTileEntity(posX, posY, posZ);
	}
}
