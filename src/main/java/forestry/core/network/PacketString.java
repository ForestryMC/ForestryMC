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

public class PacketString extends ForestryPacket {

	private String string;

	public PacketString(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketString(PacketId id, String string) {
		super(id);
		this.string = string;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeUTF(string);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		string = data.readUTF();
	}

	public String getString() {
		return string;
	}
}
