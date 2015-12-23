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

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITreeMutationCustom;
import forestry.api.arboriculture.ITreeMutationFactory;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;

public class TreeMutationFactory implements ITreeMutationFactory {

	@Override
	public ITreeMutationCustom createMutation(IAlleleTreeSpecies parent0, IAlleleTreeSpecies parent1, IAllele[] result, int chance) {
		ITreeMutationCustom mutation = new TreeMutation(parent0, parent1, result, chance);
		TreeManager.treeRoot.registerMutation(mutation);
		return mutation;
	}
}
