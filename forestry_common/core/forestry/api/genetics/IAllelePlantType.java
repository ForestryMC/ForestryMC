/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;

public interface IAllelePlantType extends IAllele {

	public EnumSet<EnumPlantType> getPlantTypes();

}
