/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.arboriculture;

public enum EnumGermlingType {
	SAPLING("Sapling"), BLOSSOM("Blossom"), POLLEN("Pollen"), GERMLING("Germling"), NONE("None");

	public static final EnumGermlingType[] VALUES = values();
	
	String name;

	private EnumGermlingType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
