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

import net.minecraft.item.ItemStack;

import forestry.core.interfaces.ISocketable;

public class PacketSocketUpdate extends PacketCoordinates {

	public ItemStack[] itemstacks;

	public PacketSocketUpdate() {
	}

	public PacketSocketUpdate(int id, int posX, int posY, int posZ, ISocketable tile) {
		super(id, posX, posY, posZ);

		itemstacks = new ItemStack[tile.getSocketCount()];
		for (int i = 0; i < tile.getSocketCount(); i++)
			itemstacks[i] = tile.getSocket(i);
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);

		data.writeShort(itemstacks.length);
		for (ItemStack itemstack : itemstacks)
			writeItemStack(itemstack, data);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);

		int sockets = data.readShort();
		itemstacks = new ItemStack[sockets];
		for (int i = 0; i < sockets; i++)
			itemstacks[i] = readItemStack(data);
	}
}
