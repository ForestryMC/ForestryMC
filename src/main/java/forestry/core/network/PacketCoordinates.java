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

import java.io.IOException;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class PacketCoordinates extends ForestryPacket implements ILocatedPacket {

	private int posX;
	private int posY;
	private int posZ;

	public PacketCoordinates(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketCoordinates(PacketId id, TileEntity tileEntity) {
		this(id, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}

	public PacketCoordinates(PacketId id, ChunkCoordinates coordinates) {
		this(id, coordinates.posX, coordinates.posY, coordinates.posZ);
	}

	public PacketCoordinates(PacketId id, int posX, int posY, int posZ) {
		super(id);
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
	protected void readData(DataInputStreamForestry data) throws IOException {
		posX = data.readVarInt();
		posY = data.readVarInt();
		posZ = data.readVarInt();
	}

	public final ChunkCoordinates getCoordinates() {
		return new ChunkCoordinates(posX, posY, posZ);
	}

	public final int getPosX() {
		return posX;
	}

	public final int getPosY() {
		return posY;
	}

	public final int getPosZ() {
		return posZ;
	}

	@Override
	public final TileEntity getTarget(World world) {
		return world.getTileEntity(posX, posY, posZ);
	}
}
