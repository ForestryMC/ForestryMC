/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
