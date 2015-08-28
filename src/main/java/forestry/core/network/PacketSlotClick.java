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

import net.minecraft.tileentity.TileEntity;

public class PacketSlotClick extends PacketCoordinates {
	private int slot;

	public PacketSlotClick(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketSlotClick(PacketId id, TileEntity tile, int slot) {
		super(id, tile);
		this.slot = slot;
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		this.slot = data.readVarInt();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeVarInt(slot);
	}

	public int getSlot() {
		return slot;
	}
}
