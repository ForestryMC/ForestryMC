/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import genetics.api.organism.IOrganismType;

public enum EnumGermlingType implements IOrganismType {
	SAPLING("sapling"), POLLEN("pollen");

	public static final EnumGermlingType[] VALUES = values();

	private final String name;

	EnumGermlingType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
