/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.world;

import net.minecraft.world.gen.feature.WorldGenerator;

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
