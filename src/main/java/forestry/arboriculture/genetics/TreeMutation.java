/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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

	private final ITreeRoot root;

	public TreeMutation(IAllele allele0, IAllele allele1, IAllele[] template, int chance) {
		super(allele0, allele1, template, chance);
		
		root = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
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
		if (biome.temperature < minTemperature || biome.temperature > maxTemperature) {
			return 0;
		}
		if (biome.rainfall < minRainfall || biome.rainfall > maxRainfall) {
			return 0;
		}

		processedChance *= PluginArboriculture.treeInterface.getTreekeepingMode(world).getMutationModifier((ITreeGenome) genome0, (ITreeGenome) genome1, 1f);

		if (this.allele0.getUID().equals(allele0.getUID()) && this.allele1.getUID().equals(allele1.getUID())) {
			return processedChance;
		}
		if (this.allele1.getUID().equals(allele0.getUID()) && this.allele0.getUID().equals(allele1.getUID())) {
			return processedChance;
		}

		return 0;
	}

}
