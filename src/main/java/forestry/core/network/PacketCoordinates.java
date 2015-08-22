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
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class PacketCoordinates extends ForestryPacket implements ILocatedPacket {

	public BlockPos pos;

	public PacketCoordinates() {
	}

	public PacketCoordinates(int id, BlockPos pos) {
		super(id);
		this.pos = pos;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {

		data.writeInt(pos.getX());
		data.writeInt(pos.getY());
		data.writeInt(pos.getZ());

	}

	@Override
	public void readData(DataInputStream data) throws IOException {

		int posX = data.readInt();
		int posY = data.readInt();
		int posZ = data.readInt();
		pos = new BlockPos(posX, posY, posZ);

	}
	public final int getPosX() {
		return pos.getX();
	}

	public final int getPosY() {
		return pos.getY();
	}

	public final int getPosZ() {
		return pos.getZ();
	}

	public BlockPos getCoordinates() {
		return pos;
	}

	@Override
	public TileEntity getTarget(World world) {
		return world.getTileEntity(pos);
	}

}
