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
package forestry.lepidopterology.genetics;

import net.minecraft.world.World;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.root.IIndividualRoot;

import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.genetics.IButterflyMutation;
import forestry.api.lepidopterology.genetics.IButterflyMutationBuilder;
import forestry.core.genetics.mutations.Mutation;

public class ButterflyMutation extends Mutation implements IButterflyMutation, IButterflyMutationBuilder {

	protected ButterflyMutation(IAlleleForestrySpecies species0, IAlleleForestrySpecies species1, IAllele[] template, int chance) {
		super(species0, species1, template, chance);
	}

	@Override
	public IButterflyMutation build() {
		return this;
	}

	@Override
	public float getChance(World world, IButterflyNursery housing, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		float processedChance = super.getChance(world, housing.getCoordinates(), allele0, allele1, genome0, genome1, housing);
		if (processedChance <= 0) {
			return 0;
		}
		return processedChance;
	}

	@Override
	public IIndividualRoot getRoot() {
		return ButterflyHelper.getRoot();
	}
}
