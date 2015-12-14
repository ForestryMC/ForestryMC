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

import net.minecraft.nbt.NBTTagCompound;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;

public abstract class PacketNBT extends ForestryPacket {

	private NBTTagCompound nbttagcompound;

	protected PacketNBT() {
	}

	protected PacketNBT(NBTTagCompound nbttagcompound) {
		this.nbttagcompound = nbttagcompound;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeNBTTagCompound(nbttagcompound);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		this.nbttagcompound = data.readNBTTagCompound();
	}

	protected NBTTagCompound getTagCompound() {
		return this.nbttagcompound;
	}
}
