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

import net.minecraft.util.BlockPos;

public class PacketUpdate extends PacketCoordinates {

	public PacketPayload payload;

	public PacketUpdate() {
	}

	public PacketUpdate(int id) {
		this(id, null);
	}

	public PacketUpdate(int id, PacketPayload payload) {
		this(id, new BlockPos(0, 0, 0), payload);
	}

	public PacketUpdate(int id, BlockPos pos, PacketPayload payload) {
		super(id, pos);

		this.payload = payload;
	}

	public PacketUpdate(int id, BlockPos pos, short val) {
		super(id, pos);

		this.payload = new PacketPayload(0, 1);
		this.payload.shortPayload[0] = val;
	}

	public PacketUpdate(int id, BlockPos pos, int val) {
		super(id, pos);

		this.payload = new PacketPayload(1, 0);
		this.payload.intPayload[0] = val;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {

		super.writeData(data);

		// No payload means no data
		if (payload == null) {
			data.writeInt(0);
			data.writeInt(0);
			data.writeInt(0);
			data.writeInt(0);
			return;
		}

		data.writeInt(payload.intPayload.length);
		data.writeInt(payload.shortPayload.length);
		data.writeInt(payload.floatPayload.length);
		data.writeInt(payload.stringPayload.length);

		for (int intData : payload.intPayload) {
			data.writeInt(intData);
		}
		for (int shortData : payload.shortPayload) {
			data.writeShort(shortData);
		}
		for (float floatData : payload.floatPayload) {
			data.writeFloat(floatData);
		}
		for (String stringData : payload.stringPayload) {
			data.writeUTF(stringData);
		}

	}

	@Override
	public void readData(DataInputStream data) throws IOException {

		super.readData(data);

		payload = new PacketPayload();

		payload.intPayload = new int[data.readInt()];
		payload.shortPayload = new short[data.readInt()];
		payload.floatPayload = new float[data.readInt()];
		payload.stringPayload = new String[data.readInt()];

		for (int i = 0; i < payload.intPayload.length; i++) {
			payload.intPayload[i] = data.readInt();
		}
		for (int i = 0; i < payload.shortPayload.length; i++) {
			payload.shortPayload[i] = data.readShort();
		}
		for (int i = 0; i < payload.floatPayload.length; i++) {
			payload.floatPayload[i] = data.readFloat();
		}
		for (int i = 0; i < payload.stringPayload.length; i++) {
			payload.stringPayload[i] = data.readUTF();
		}

	}

}
