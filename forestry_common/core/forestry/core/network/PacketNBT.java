/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
