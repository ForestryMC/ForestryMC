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

public abstract class PacketGuiSelect extends ForestryPacket {
	private int primaryIndex;
	private int secondaryIndex;

	protected PacketGuiSelect() {
	}

	protected PacketGuiSelect(IPacketId id, int primaryIndex, int secondaryIndex) {
		super(id);
		this.primaryIndex = primaryIndex;
		this.secondaryIndex = secondaryIndex;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeInt(primaryIndex);
		data.writeInt(secondaryIndex);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		primaryIndex = data.readInt();
		secondaryIndex = data.readInt();
	}

	public int getPrimaryIndex() {
		return primaryIndex;
	}

	public int getSecondaryIndex() {
		return secondaryIndex;
	}

}
