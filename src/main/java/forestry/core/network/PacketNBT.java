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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class PacketNBT extends ForestryPacket {

	protected NBTTagCompound nbttagcompound;

	public PacketNBT() {
	}

	public PacketNBT(int id) {
		super(id);
	}

	public PacketNBT(int id, NBTTagCompound nbttagcompound) {
		super(id);
		this.nbttagcompound = nbttagcompound;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		byte[] compressed = CompressedStreamTools.compress(nbttagcompound);
		data.writeShort(compressed.length);
		data.write(compressed);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		short length = data.readShort();
		byte[] compressed = new byte[length];
		data.readFully(compressed);
		this.nbttagcompound = CompressedStreamTools.readCompressed(new ByteArrayInputStream(compressed));
	}

	public NBTTagCompound getTagCompound() {
		return this.nbttagcompound;
	}
}
