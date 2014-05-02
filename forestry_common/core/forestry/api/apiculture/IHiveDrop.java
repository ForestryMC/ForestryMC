/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Bees can be seeded either as hive drops or as mutation results.
 * 
 * Add IHiveDrops to BeeManager.hiveDrops
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
