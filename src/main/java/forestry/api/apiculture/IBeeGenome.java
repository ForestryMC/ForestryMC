/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.IGenome;

/**
 * Only the default implementation is supported.
 *
 * @author SirSengir
 *
 * @apiNote Please use {@link forestry.api.genetics.ISpeciesRoot#getWrapper(IGenome)} to wrap this genome to pure
 * instance of {@link IBeeGenomeWrapper}.
 */
public interface IBeeGenome extends IGenome, IBeeGenomeWrapper {
}
