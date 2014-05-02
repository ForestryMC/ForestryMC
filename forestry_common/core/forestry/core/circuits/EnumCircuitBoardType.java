/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.circuits;

public enum EnumCircuitBoardType {
	BASIC((short) 1, 0x191919, 0x6dcff6), ENHANCED((short) 2, 0x191919, 0xcb7c32), REFINED((short) 3, 0x191919, 0xc9c9c9), INTRICATE((short) 4, 0x191919,
			0xe2cb6b);

	final short sockets;
	final int primaryColor;
	final int secondaryColor;

	private EnumCircuitBoardType(short sockets, int primaryColor, int secondaryColor) {
		this.sockets = sockets;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
	}
}
