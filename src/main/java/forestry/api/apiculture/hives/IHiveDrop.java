/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.hives;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import forestry.api.apiculture.genetics.IBee;

/**
 * Bees can be seeded either as hive drops or as mutation results.
 * <p>
 * Add IHiveDrops with HiveManager.get___Hive.addDrop
 *
 * @author SirSengir
 */
public interface IHiveDrop {

	IBee getBeeType(BlockGetter world, BlockPos pos);

	NonNullList<ItemStack> getExtraItems(BlockGetter world, BlockPos pos, int fortune);

	/**
	 * Chance to drop a bee or extra items. Default drops have 0.80 (= 80 %).
	 *
	 * @param world Minecraft world this is called for.
	 * @param pos   Coordinates of the broken hive.
	 * @return Chance for drop as a float of 0.0 - 1.0.
	 */
	double getChance(BlockGetter world, BlockPos pos, int fortune);

	/**
	 * Chance for the princess to be ignoble. Default is around 0.4 to 0.7 (40% - 70%).
	 *
	 * @param world Minecraft world this is called for.
	 * @param pos   Coordinates of the broken hive.
	 * @return Chance for ignoble as a float of 0.0 - 1.0.
	 */
	double getIgnobleChance(BlockGetter world, BlockPos pos, int fortune);
}
