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

import net.minecraft.nbt.NBTTagCompound;

public class PacketNBT extends ForestryPacket {

	protected NBTTagCompound nbttagcompound;

	public PacketNBT(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketNBT(PacketId id, NBTTagCompound nbttagcompound) {
		super(id);
		this.nbttagcompound = nbttagcompound;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeNBTTagCompound(nbttagcompound);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		this.nbttagcompound = data.readNBTTagCompound();
	}

	public NBTTagCompound getTagCompound() {
		return this.nbttagcompound;
	}
}
