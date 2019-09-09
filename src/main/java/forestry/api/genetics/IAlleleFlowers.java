/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;


import genetics.api.alleles.IAllele;

public interface IAlleleFlowers extends IAllele {

	/**
	 * @return FlowerProvider
	 */
	IFlowerProvider getProvider();

}
