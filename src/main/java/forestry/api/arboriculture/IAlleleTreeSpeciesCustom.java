/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.IAlleleSpeciesCustom;
import forestry.api.genetics.IFruitFamily;

public interface IAlleleTreeSpeciesCustom extends IAlleleSpeciesCustom, IAlleleTreeSpecies {

	/** Add a fruit family for this tree. Trees can have multiple fruit families. */
	IAlleleTreeSpeciesCustom addFruitFamily(IFruitFamily family);

	/** Set the minecraft plant type for this tree. Default is Plains. */
	IAlleleTreeSpeciesCustom setPlantType(EnumPlantType type);

}
