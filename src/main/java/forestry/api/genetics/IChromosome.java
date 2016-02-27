/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nonnull;

import forestry.api.core.INbtWritable;

/**
 * Implementations other than Forestry's default one are not supported!
 *
 * @author SirSengir
 */
public interface IChromosome extends INbtWritable {

	@Nonnull
	IAllele getPrimaryAllele();

	@Nonnull
	IAllele getSecondaryAllele();

	@Nonnull
	IAllele getInactiveAllele();

	@Nonnull
	IAllele getActiveAllele();

}
