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

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.core.genetics.Mutation;
import forestry.plugins.PluginArboriculture;

public class TreeMutation extends Mutation implements ITreeMutation {

	private final ITreeRoot root;

	public TreeMutation(IAlleleSpecies allele0, IAlleleSpecies allele1, IAllele[] template, int chance) {
		super(allele0, allele1, template, chance);
		
		root = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
		PluginArboriculture.treeInterface.registerMutation(this);
	}

	@Override
	public ITreeRoot getRoot() {
		return root;
	}

	@Override
	public float getChance(World world, int x, int y, int z, IAlleleTreeSpecies allele0, IAlleleTreeSpecies allele1, ITreeGenome genome0, ITreeGenome genome1) {
		float processedChance = super.getChance(world, x, y, z, allele0, allele1, genome0, genome1);
		if (processedChance <= 0) {
			return 0;
		}

		processedChance *= PluginArboriculture.treeInterface.getTreekeepingMode(world).getMutationModifier(genome0, genome1, 1f);

		return processedChance;
	}

}
