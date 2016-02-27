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

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.ITreeMutationBuilder;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.TreeChromosome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.core.genetics.mutations.Mutation;

public class TreeMutation extends Mutation<TreeChromosome> implements ITreeMutation, ITreeMutationBuilder {

	public TreeMutation(IAlleleTreeSpecies allele0, IAlleleTreeSpecies allele1, ImmutableMap<TreeChromosome, IAllele> template, int chance) {
		super(allele0, allele1, template, chance);
	}

	// TODO: break this into a separate builder class
	@Nonnull
	@Override
	public ITreeMutation build() {
		TreeManager.treeRoot.registerMutation(this);
		return this;
	}

	@Nonnull
	@Override
	public ITreeRoot getRoot() {
		return TreeManager.treeRoot;
	}
}
