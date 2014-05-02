/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.Locale;

public enum EnumBeeType {
	DRONE, PRINCESS, QUEEN, LARVAE, NONE;

	public static final EnumBeeType[] VALUES = values();

	String name;

	private EnumBeeType() {
		this.name = "bees." + this.toString().toLowerCase(Locale.ENGLISH);
	}

	public String getName() {
		return name;
	}
}
