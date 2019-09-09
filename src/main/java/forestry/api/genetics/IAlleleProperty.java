/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import genetics.api.alleles.IAllele;

public interface IAlleleProperty<A extends IAlleleProperty<A>> extends IAllele, Comparable<A> {

	/**
	 * To compare the allele for the properties
	 */
	@Override
	int compareTo(A o);

}
