/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.world;

import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * @deprecated since Forestry 4.1.0. Unused
 */
@Deprecated
public interface IWorldGenInterface {

	/**
	 * Retrieves generators for trees identified by a given string.
	 * 
	 * Returned generator classes take an {@link ITreeGenData} in the constructor.
	 * 
	 * @param ident
	 *            Unique identifier for tree type. Forestry's convention is 'treeSpecies', i.e. 'treeBaobab', 'treeSequoia'.
	 * @return All generators matching the given ident.
	 */
	Class<? extends WorldGenerator>[] getTreeGenerators(String ident);
}
