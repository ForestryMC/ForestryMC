/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.arboriculture;

import forestry.api.genetics.IAllele;

/**
 * Simple allele encapsulating an {@link IFruitProvider}.
 */
public interface IAlleleFruit extends IAllele {

	IFruitProvider getProvider();

}
