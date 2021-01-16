/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.genetics;

import java.util.Locale;

import genetics.api.organism.IOrganismType;

public enum EnumBeeType implements IOrganismType {
	DRONE, PRINCESS, QUEEN, LARVAE;

	public static final EnumBeeType[] VALUES = values();

	private final String name;

	EnumBeeType() {
		this.name = this.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String getName() {
		return name;
	}
}
