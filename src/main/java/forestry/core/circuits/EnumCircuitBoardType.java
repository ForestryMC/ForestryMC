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
package forestry.core.circuits;

public enum EnumCircuitBoardType {
	BASIC((short) 1, 0x191919, 0x6dcff6), ENHANCED((short) 2, 0x191919, 0xcb7c32), REFINED((short) 3, 0x191919, 0xc9c9c9), INTRICATE((short) 4, 0x191919,
			0xe2cb6b);

	private final short sockets;
	private final int primaryColor;
	private final int secondaryColor;

	EnumCircuitBoardType(short sockets, int primaryColor, int secondaryColor) {
		this.sockets = sockets;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
	}

	public short getSockets() {
		return sockets;
	}

	public int getPrimaryColor() {
		return primaryColor;
	}

	public int getSecondaryColor() {
		return secondaryColor;
	}
}
