/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import forestry.api.genetics.IAlleleComparable;

/**
 * Simple allele encapsulating an {@link IFruitProvider}.
 */
public interface IAlleleFruit extends IAlleleComparable<IAlleleFruit> {

	IFruitProvider getProvider();
	
	String getModelName();
	
	String getModID();

}
