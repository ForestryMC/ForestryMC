/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.world;

import net.minecraft.world.World;

public interface ITreeGenData {

	int getGirth(World world, int x, int y, int z);

	float getHeightModifier();

	boolean canGrow(World world, int x, int y, int z, int expectedGirth, int expectedHeight);

	void setLeaves(World world, String owner, int x, int y, int z);

	boolean allowsFruitBlocks();

	boolean trySpawnFruitBlock(World world, int x, int y, int z);
}
