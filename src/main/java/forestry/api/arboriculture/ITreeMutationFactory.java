/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import forestry.api.genetics.IAllele;

public interface ITreeMutationFactory {
	/**
	 * Creates a new tree mutation.
	 * Automatically registered with TreeManager.treeRoot.registerMutation()
	 * See ITreeMutationCustom and IMutationCustom for adding additional properties to the returned mutation.
	 *
	 * @param parentTree0 A parent tree for this mutation
	 * @param parentTree1 A parent tree for this mutation
	 * @param result The resulting alleles for this mutation
	 * @param chance The chance that breeding the two parent trees will result in this mutation
	 * @return a new tree mutation.
	 */
	ITreeMutationCustom createMutation(IAlleleTreeSpecies parentTree0, IAlleleTreeSpecies parentTree1, IAllele[] result, int chance);
}
