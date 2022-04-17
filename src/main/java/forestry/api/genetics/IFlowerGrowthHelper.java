/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.world.World;

public interface IFlowerGrowthHelper {
	/**
	 * Plants a random flower from flowerType at the specified position.
	 * @return true if successful, false if it could not be planted.
	 */
	boolean plantRandomFlower(String flowerType, World world, int x, int y, int z);
}
