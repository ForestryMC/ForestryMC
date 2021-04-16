/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.world.biome.Biome;

/**
 * Provides information about the biome an object is based in.
 */
public interface IBiomeProvider {
	/**
	 * @return The biome the object that implements this interface is located in.
	 */
	Biome getBiome();
}
