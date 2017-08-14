/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

/**
 * Called after Forestry has registered all his species alleles of a individual.
 */
public class AlleleSpeciesRegisterEvent<A extends IAlleleSpecies> extends AlleleRegisterEvent<A> {

	private final ISpeciesRoot root;

	public AlleleSpeciesRegisterEvent(ISpeciesRoot root, Class<? extends A> alleleClass) {
		super(alleleClass);
		this.root = root;
	}

	public ISpeciesRoot getRoot() {
		return root;
	}

}
