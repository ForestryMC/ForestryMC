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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class PacketTileNBT extends PacketNBT implements ILocatedPacket {

	private BlockPos pos;

	public PacketTileNBT() {
	}

	public PacketTileNBT(int id, TileEntity tile) {
		super(id);
		
		pos = tile.getPos();

		this.nbttagcompound = new NBTTagCompound();
		tile.writeToNBT(nbttagcompound);
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {

		data.writeInt(pos.getX());
		data.writeInt(pos.getY());
		data.writeInt(pos.getZ());

		super.writeData(data);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {

		int posX = data.readInt();
		int posY = data.readInt();
		int posZ = data.readInt();
		pos = new BlockPos(posX, posY, posZ);

		super.readData(data);
	}

	@Override
	public TileEntity getTarget(World world) {
		return world.getTileEntity(pos);
	}

}
