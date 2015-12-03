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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.ILocatedPacket;

public abstract class PacketCoordinates extends ForestryPacket implements ILocatedPacket {

	private int posX;
	private int posY;
	private int posZ;

	public PacketCoordinates() {
	}

	protected PacketCoordinates(TileEntity tileEntity) {
		this(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}

	protected PacketCoordinates(ChunkCoordinates coordinates) {
		this(coordinates.posX, coordinates.posY, coordinates.posZ);
	}

	protected PacketCoordinates(int posX, int posY, int posZ) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(posX);
		data.writeVarInt(posY);
		data.writeVarInt(posZ);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		posX = data.readVarInt();
		posY = data.readVarInt();
		posZ = data.readVarInt();
	}

	protected final ChunkCoordinates getCoordinates() {
		return new ChunkCoordinates(posX, posY, posZ);
	}

	@Override
	public final int getPosX() {
		return posX;
	}

	@Override
	public final int getPosY() {
		return posY;
	}

	@Override
	public final int getPosZ() {
		return posZ;
	}

	public final TileEntity getTarget(World world) {
		return world.getTileEntity(posX, posY, posZ);
	}
}
