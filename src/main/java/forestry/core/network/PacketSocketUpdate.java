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

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import forestry.core.interfaces.ISocketable;

public class PacketSocketUpdate extends PacketCoordinates {

	public ItemStack[] itemStacks;

	public PacketSocketUpdate(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public <T extends TileEntity & ISocketable> PacketSocketUpdate(PacketId id, T tile) {
		super(id, tile);

		itemStacks = new ItemStack[tile.getSocketCount()];
		for (int i = 0; i < tile.getSocketCount(); i++) {
			itemStacks[i] = tile.getSocket(i);
		}
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);

		data.writeItemStacks(itemStacks);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);

		itemStacks = data.readItemStacks();
	}
}
