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

public class PacketInventoryStack extends PacketCoordinates {

	public int slotIndex;
	public ItemStack itemstack;

	public PacketInventoryStack() {
	}

	public PacketInventoryStack(int id, int posX, int posY, int posZ, int slotIndex, ItemStack itemstack) {
		super(id, posX, posY, posZ);
		this.slotIndex = slotIndex;
		this.itemstack = itemstack;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeInt(slotIndex);
		writeItemStack(itemstack, data);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		slotIndex = data.readInt();
		itemstack = readItemStack(data);
	}

}
