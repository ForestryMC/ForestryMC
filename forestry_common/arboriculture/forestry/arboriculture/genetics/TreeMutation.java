/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.genetics;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.core.genetics.Mutation;
import forestry.plugins.PluginArboriculture;

public class TreeMutation extends Mutation implements ITreeMutation {

	private ITreeRoot root;

	public TreeMutation(IAllele allele0, IAllele allele1, IAllele[] template, int chance) {
		super(allele0, allele1, template, chance);
		
		root = (ITreeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
		PluginArboriculture.treeInterface.registerMutation(this);
	}

	@Override
	public ITreeRoot getRoot() {
		return root;
	}
	
	@Override
	public float getChance(World world, int x, int y, int z, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		float processedChance = chance;

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(x, z);
		if (biome.temperature < minTemperature || biome.temperature > maxTemperature)
			return 0;
		if (biome.rainfall < minRainfall || biome.rainfall > maxRainfall)
			return 0;

		processedChance *= PluginArboriculture.treeInterface.getTreekeepingMode(world).getMutationModifier((ITreeGenome) genome0, (ITreeGenome) genome1, 1f);

		if (this.allele0.getUID().equals(allele0.getUID()) && this.allele1.getUID().equals(allele1.getUID()))
			return processedChance;
		if (this.allele1.getUID().equals(allele0.getUID()) && this.allele0.getUID().equals(allele1.getUID()))
			return processedChance;

		return 0;
	}

}
