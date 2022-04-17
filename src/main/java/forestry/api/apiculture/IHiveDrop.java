/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Bees can be seeded either as hive drops or as mutation results.
 * 
 * Add IHiveDrops with HiveManager.get___Hive.addDrop
 * 
 * @author SirSengir
 */
public interface IHiveDrop {

	ItemStack getPrincess(World world, int x, int y, int z, int fortune);

	Collection<ItemStack> getDrones(World world, int x, int y, int z, int fortune);

	Collection<ItemStack> getAdditional(World world, int x, int y, int z, int fortune);

	/**
	 * Chance to drop. Default drops have 80 (= 80 %).
	 * 
	 * @param world Minecraft world this is called for.
	 * @param x x-Coordinate of the broken hive.
	 * @param y y-Coordinate of the broken hive.
	 * @param z z-Coordinate of the broken hive.
	 * @return Chance for drop as an integer of 0 - 100.
	 */
	int getChance(World world, int x, int y, int z);
}
