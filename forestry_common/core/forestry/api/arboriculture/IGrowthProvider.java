/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.world.World;

public interface IGrowthProvider {

	/**
	 * Check to see whether a sapling at the given location with the given genome can grow into a tree.
	 * 
	 * @param genome Genome of the tree this is called for.
	 * @param world Minecraft world the tree will inhabit.
	 * @param xPos x-Coordinate to attempt growth at.
	 * @param yPos y-Coordinate to attempt growth at.
	 * @param zPos z-Coordinate to attempt growth at.
	 * @param expectedGirth Trunk size of the tree to generate.
	 * @param expectedHeight Height of the tree to generate.
	 * @return true if the tree can grow at the given coordinates, false otherwise.
	 */
	boolean canGrow(ITreeGenome genome, World world, int xPos, int yPos, int zPos, int expectedGirth, int expectedHeight);

	EnumGrowthConditions getGrowthConditions(ITreeGenome genome, World world, int xPos, int yPos, int zPos);

	/**
	 * @return Short, human-readable identifier used in the treealyzer.
	 */
	String getDescription();

	/**
	 * @return Detailed description of growth behaviour used in the treealyzer.
	 */
	String[] getInfo();

}
