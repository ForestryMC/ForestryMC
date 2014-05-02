/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.IAllelePlantType;

public class AllelePlantType extends Allele implements IAllelePlantType {

	private EnumSet<EnumPlantType> types;

	protected AllelePlantType(String uid, EnumPlantType type) {
		this(uid, type, false);
	}

	protected AllelePlantType(String uid, EnumPlantType type, boolean isDominant) {
		this(uid, EnumSet.of(type), isDominant);
	}

	protected AllelePlantType(String uid, EnumSet<EnumPlantType> types, boolean isDominant) {
		super(uid, isDominant);
		this.types = types;
	}

	public EnumSet<EnumPlantType> getPlantTypes() {
		return types;
	}
}
