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

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterflyMutation;
import forestry.api.lepidopterology.IButterflyMutationBuilder;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.core.genetics.mutations.Mutation;

public class ButterflyMutation extends Mutation implements IButterflyMutation, IButterflyMutationBuilder {

	protected ButterflyMutation(IAlleleSpecies species0, IAlleleSpecies species1, IAllele[] template, int chance) {
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
	public ISpeciesRoot getRoot() {
		return ButterflyManager.butterflyRoot;
	}

}
