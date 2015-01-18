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
package forestry.core.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class PacketCoordinates extends ForestryPacket implements ILocatedPacket {

	public int posX;
	public int posY;
	public int posZ;

	public PacketCoordinates() {
	}

	public PacketCoordinates(int id, ChunkCoordinates coordinates) {
		this(id, coordinates.posX, coordinates.posY, coordinates.posZ);
	}

	public PacketCoordinates(int id, int posX, int posY, int posZ) {
		super(id);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {

		data.writeInt(posX);
		data.writeInt(posY);
		data.writeInt(posZ);

	}

	@Override
	public void readData(DataInputStream data) throws IOException {

		posX = data.readInt();
		posY = data.readInt();
		posZ = data.readInt();

	}

	public ChunkCoordinates getCoordinates() {
		return new ChunkCoordinates(posX, posY, posZ);
	}

	@Override
	public TileEntity getTarget(World world) {
		return world.getTileEntity(posX, posY, posZ);
	}

}
