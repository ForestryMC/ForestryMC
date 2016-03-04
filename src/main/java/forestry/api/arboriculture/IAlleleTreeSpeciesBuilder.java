/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.IAlleleSpeciesBuilder;
import forestry.api.genetics.IFruitFamily;

public interface IAlleleTreeSpeciesBuilder extends IAlleleSpeciesBuilder {

	@Override
	IAlleleTreeSpecies build();

	/** Add a fruit family for this tree. Trees can have multiple fruit families. */
	IAlleleTreeSpeciesBuilder addFruitFamily(IFruitFamily family);

	/** Set the minecraft plant type for this tree. Default is Plains. */
	IAlleleTreeSpeciesBuilder setPlantType(EnumPlantType type);

}
