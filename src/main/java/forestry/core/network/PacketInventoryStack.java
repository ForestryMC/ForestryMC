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

public class PacketInventoryStack extends PacketCoordinates {

	public int slotIndex;
	public ItemStack itemstack;

	public PacketInventoryStack(DataInputStream data) throws IOException {
		super(data);
	}

	public PacketInventoryStack(PacketId id, int posX, int posY, int posZ, int slotIndex, ItemStack itemstack) {
		super(id, posX, posY, posZ);
		this.slotIndex = slotIndex;
		this.itemstack = itemstack;
	}

	@Override
	protected void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeInt(slotIndex);
		PacketHelper.writeItemStack(itemstack, data);
	}

	@Override
	protected void readData(DataInputStream data) throws IOException {
		super.readData(data);
		slotIndex = data.readInt();
		itemstack = PacketHelper.readItemStack(data);
	}
}
