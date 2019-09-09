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

import genetics.api.alleles.IAllele;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.components.ComponentKeys;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITreeMutationBuilder;
import forestry.api.arboriculture.genetics.ITreeMutationFactory;

public class TreeMutationFactory implements ITreeMutationFactory {

	@Override
	public ITreeMutationBuilder createMutation(IAlleleTreeSpecies parent0, IAlleleTreeSpecies parent1, IAllele[] result, int chance) {
		TreeMutation treeMutation = new TreeMutation(parent0, parent1, result, chance);
		IMutationContainer<IBee, IMutation> container = BeeManager.beeRoot.getComponent(ComponentKeys.MUTATIONS);
		container.registerMutation(treeMutation);
		return treeMutation;
	}
}
