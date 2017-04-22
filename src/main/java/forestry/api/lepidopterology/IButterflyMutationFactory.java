/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import forestry.api.genetics.IAllele;

public interface IButterflyMutationFactory {
	/**
	 * Creates a new butterfly mutation.
	 * Automatically registered with ButterflyManager.butterflyRoot.registerMutation()
	 * See IButterflyMutationBuilder and IMutationBuilder for adding additional properties to the returned mutation.
	 *
	 * @param parentButterfly0 A parent butterfly for this mutation
	 * @param parentButterfly1 A parent butterfly for this mutation
	 * @param result           The resulting alleles for this mutation
	 * @param chance           The chance that breeding the two parent butterfly's will result in this mutation
	 * @return a new butterfly mutation.
	 */
	IButterflyMutationBuilder createMutation(IAlleleButterflySpecies parentButterfly0, IAlleleButterflySpecies parentButterfly1, IAllele[] result, int chance);
}
