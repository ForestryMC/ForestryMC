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
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.ILocatedPacket;

public abstract class PacketCoordinates extends ForestryPacket implements ILocatedPacket {

	private BlockPos pos;

	public PacketCoordinates() {
	}

	protected PacketCoordinates(TileEntity tileEntity) {
		this(tileEntity.getPos());
	}

	protected PacketCoordinates(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(pos.getX());
		data.writeVarInt(pos.getY());
		data.writeVarInt(pos.getZ());
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		int posX = data.readVarInt();
		int posY = data.readVarInt();
		int posZ = data.readVarInt();
		pos = new BlockPos(posX, posY, posZ);
	}

	@Override
	public final BlockPos getPos() {
		return pos;
	}

	public final TileEntity getTarget(World world) {
		return world.getTileEntity(pos);
	}
}
