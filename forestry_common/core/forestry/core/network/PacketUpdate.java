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

public class PacketUpdate extends PacketCoordinates {

	public PacketPayload payload;

	public PacketUpdate() {
	}

	public PacketUpdate(int id) {
		this(id, null);
	}

	public PacketUpdate(int id, PacketPayload payload) {
		this(id, 0, 0, 0, payload);
	}

	public PacketUpdate(int id, int posX, int posY, int posZ, PacketPayload payload) {
		super(id, posX, posY, posZ);

		this.payload = payload;
	}

	public PacketUpdate(int id, int posX, int posY, int posZ, short val) {
		super(id, posX, posY, posZ);

		this.payload = new PacketPayload(0, 1);
		this.payload.shortPayload[0] = val;
	}

	public PacketUpdate(int id, int posX, int posY, int posZ, int val) {
		super(id, posX, posY, posZ);

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

		for (int intData : payload.intPayload)
			data.writeInt(intData);
		for (int shortData : payload.shortPayload)
			data.writeShort(shortData);
		for (float floatData : payload.floatPayload)
			data.writeFloat(floatData);
		for (String stringData : payload.stringPayload)
			data.writeUTF(stringData);

	}

	@Override
	public void readData(DataInputStream data) throws IOException {

		super.readData(data);

		payload = new PacketPayload();

		payload.intPayload = new int[data.readInt()];
		payload.shortPayload = new short[data.readInt()];
		payload.floatPayload = new float[data.readInt()];
		payload.stringPayload = new String[data.readInt()];

		for (int i = 0; i < payload.intPayload.length; i++)
			payload.intPayload[i] = data.readInt();
		for (int i = 0; i < payload.shortPayload.length; i++)
			payload.shortPayload[i] = data.readShort();
		for (int i = 0; i < payload.floatPayload.length; i++)
			payload.floatPayload[i] = data.readFloat();
		for (int i = 0; i < payload.stringPayload.length; i++)
			payload.stringPayload[i] = data.readUTF();

	}

}
