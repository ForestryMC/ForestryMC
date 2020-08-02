/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import net.minecraftforge.common.PlantType;

import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ILeafProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.alleles.IAlleleSpeciesBuilder;

public interface IAlleleTreeSpeciesBuilder extends IAlleleSpeciesBuilder<IAlleleTreeSpeciesBuilder> {

    @Override
    IAlleleTreeSpecies build();

    /**
     * World generator of the tree
     */
    IAlleleTreeSpeciesBuilder setGenerator(ITreeGenerator generator);

    /**
     * The germling model provider for this species
     */
    IAlleleTreeSpeciesBuilder setModel(IGermlingModelProvider germlingModelProvider);

    /**
     * Provider for the leaf blocks of this species
     */
    IAlleleTreeSpeciesBuilder setLeaf(ILeafProvider leafProvider);

    /**
     * The leaf sprite provider for this species
     */
    IAlleleTreeSpeciesBuilder setLeafSprite(ILeafSpriteProvider leafSpriteProvider);

    /**
     * Add a fruit family for this tree. Trees can have multiple fruit families.
     */
    IAlleleTreeSpeciesBuilder addFruitFamily(IFruitFamily family);

    /**
     * Set the minecraft plant type for this tree. Default is Plains.
     */
    IAlleleTreeSpeciesBuilder setPlantType(PlantType type);

    /**
     * Set rarity of the species, will affect spawn rate in the world. Must be a float between 0 and 1. If it's 0, it will not spawn.
     */
    IAlleleTreeSpeciesBuilder setRarity(float rarity);

    /**
     * Set the growth provider.
     */
    IAlleleTreeSpeciesBuilder setGrowthProvider(IGrowthProvider growthProvider);

}
