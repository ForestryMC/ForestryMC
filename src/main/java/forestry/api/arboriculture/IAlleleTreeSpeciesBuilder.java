/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import forestry.api.genetics.IAlleleSpeciesBuilder;
import forestry.api.genetics.IFruitFamily;
import net.minecraftforge.common.EnumPlantType;

public interface IAlleleTreeSpeciesBuilder extends IAlleleSpeciesBuilder {

	@Override
	IAlleleTreeSpecies build();

	/**
	 * Add a fruit family for this tree. Trees can have multiple fruit families.
	 */
	IAlleleTreeSpeciesBuilder addFruitFamily(IFruitFamily family);

	/**
	 * Set the minecraft plant type for this tree. Default is Plains.
	 */
	IAlleleTreeSpeciesBuilder setPlantType(EnumPlantType type);
	
	/**
	 * Set rarity of the species, will affect spawn rate in the world. Must be a float between 0 and 1. If it's 0, it will not spawn.
	 */
	IAlleleTreeSpeciesBuilder setRarity(float rarity);
	
	/**
	 * Set the growth provider.
	 */
	IAlleleTreeSpeciesBuilder setGrowthProvider(IGrowthProvider growthProvider);

}
