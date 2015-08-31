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
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class PacketCoordinates extends ForestryPacket implements ILocatedPacket {

	private BlockPos pos;

	public PacketCoordinates(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketCoordinates(PacketId id, TileEntity tileEntity) {
		this(id, tileEntity.getPos());
	}

	public PacketCoordinates(PacketId id, BlockPos pos) {
		super(id);
		this.pos = pos;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(pos.getX());
		data.writeVarInt(pos.getY());
		data.writeVarInt(pos.getZ());
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		int posX = data.readVarInt();
		int posY = data.readVarInt();
		int posZ = data.readVarInt();
		pos = new BlockPos(posX, posY, posZ);
	}

	@Override
	public final BlockPos getPos() {
		return pos;
	}

	@Override
	public final int getPosX() {
		return pos.getX();
	}

	@Override
	public final int getPosY() {
		return pos.getY();
	}

	@Override
	public final int getPosZ() {
		return pos.getZ();
	}

	@Override
	public final TileEntity getTarget(World world) {
		return world.getTileEntity(pos);
	}
}
