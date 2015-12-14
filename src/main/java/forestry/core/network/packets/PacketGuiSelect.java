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

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;

public abstract class PacketGuiSelect extends ForestryPacket {
	private int primaryIndex;
	private int secondaryIndex;

	protected PacketGuiSelect() {
	}

	protected PacketGuiSelect(int primaryIndex, int secondaryIndex) {
		this.primaryIndex = primaryIndex;
		this.secondaryIndex = secondaryIndex;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeVarInt(primaryIndex);
		data.writeVarInt(secondaryIndex);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		primaryIndex = data.readVarInt();
		secondaryIndex = data.readVarInt();
	}

	public int getPrimaryIndex() {
		return primaryIndex;
	}

	public int getSecondaryIndex() {
		return secondaryIndex;
	}

}
