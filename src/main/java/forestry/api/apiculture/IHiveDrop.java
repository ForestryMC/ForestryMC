/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Bees can be seeded either as hive drops or as mutation results.
 * <p>
 * Add IHiveDrops with HiveManager.get___Hive.addDrop
 *
 * @author SirSengir
 */
public interface IHiveDrop {

	IBee getBeeType(IBlockAccess world, BlockPos pos);

	NonNullList<ItemStack> getExtraItems(IBlockAccess world, BlockPos pos, int fortune);

	/**
	 * Chance to drop a bee or extra items. Default drops have 0.80 (= 80 %).
	 *
	 * @param world Minecraft world this is called for.
	 * @param pos   Coordinates of the broken hive.
	 * @return Chance for drop as a float of 0.0 - 1.0.
	 */
	double getChance(IBlockAccess world, BlockPos pos, int fortune);

	/**
	 * Chance for the princess to be ignoble. Default is around 0.4 to 0.7 (40% - 70%).
	 *
	 * @param world Minecraft world this is called for.
	 * @param pos   Coordinates of the broken hive.
	 * @return Chance for ignoble as a float of 0.0 - 1.0.
	 */
	double getIgnobleChance(IBlockAccess world, BlockPos pos, int fortune);
}
