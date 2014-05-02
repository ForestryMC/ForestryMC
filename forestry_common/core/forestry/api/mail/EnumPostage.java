/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.mail;

public enum EnumPostage {
	P_0(0), P_1(1), P_2(2), P_5(5), P_10(10), P_20(20), P_50(50), P_100(100), P_200(200);

	private final int value;

	private EnumPostage(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
