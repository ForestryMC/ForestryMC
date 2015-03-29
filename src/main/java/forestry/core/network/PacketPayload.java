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

import forestry.core.utils.Utils;

public class PacketPayload {
	public int[] intPayload = new int[0];
	public short[] shortPayload = new short[0];
	public float[] floatPayload = new float[0];
	public String[] stringPayload = new String[0];

	public PacketPayload() {
	}

	public PacketPayload(int intSize, int floatSize, int stringSize) {
		intPayload = new int[intSize];
		floatPayload = new float[floatSize];
		stringPayload = new String[stringSize];
	}

	public PacketPayload(int intSize, int shortSize) {
		intPayload = new int[intSize];
		shortPayload = new short[shortSize];
	}

	public void append(PacketPayload other) {
		if (other == null) {
			return;
		}

		if (other.intPayload.length > 0) {
			this.intPayload = Utils.concat(this.intPayload, other.intPayload);
		}
		if (other.shortPayload.length > 0) {
			this.shortPayload = Utils.concat(this.shortPayload, other.shortPayload);
		}
		if (other.floatPayload.length > 0) {
			this.floatPayload = Utils.concat(this.floatPayload, other.floatPayload);
		}
		if (other.stringPayload.length > 0) {
			this.stringPayload = Utils.concat(this.stringPayload, other.stringPayload);
		}

	}

	public void append(int[] other) {
		if (other == null || other.length < 0) {
			return;
		}

		this.intPayload = Utils.concat(this.intPayload, other);
	}

	public boolean isEmpty() {
		return intPayload.length == 0 && shortPayload.length == 0 && floatPayload.length == 0 && stringPayload.length == 0;
	}
}
