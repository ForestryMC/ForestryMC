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

import net.minecraft.tileentity.TileEntity;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;

public abstract class PacketSlotClick extends PacketCoordinates {
	private int slot;

	protected PacketSlotClick() {
	}

	protected PacketSlotClick(TileEntity tile, int slot) {
		super(tile);
		this.slot = slot;
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
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
