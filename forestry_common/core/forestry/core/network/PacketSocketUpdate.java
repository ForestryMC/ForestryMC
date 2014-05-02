/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
		for (int i = 0; i < itemstacks.length; i++)
			writeItemStack(itemstacks[i], data);
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
