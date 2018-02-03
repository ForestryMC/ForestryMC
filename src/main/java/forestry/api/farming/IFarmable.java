/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * IFarmable describes a crop or other harvestable object and can be used to inspect item stacks and blocks for matches.
 */
public interface IFarmable {

	/**
	 * @return true if the block at the given location is a "sapling" for this type, i.e. a non-harvestable immature version of the crop.
	 * @deprecated Since Forestry 5.8. Use the version below. TODO Remove this method in 1.13
	 */
	@Deprecated
	default boolean isSaplingAt(World world, BlockPos pos){
		return false;
	}

	/**
	 * @return true if the block at the given location is a "sapling" for this type, i.e. a non-harvestable immature version of the crop.
	 */
	default boolean isSaplingAt(World world, BlockPos pos, IBlockState blockState){
		return isSaplingAt(world, pos);
	}

	/**
	 * @return {@link ICrop} if the block at the given location is a harvestable and mature crop, null otherwise.
	 */
	@Nullable
	ICrop getCropAt(World world, BlockPos pos, IBlockState blockState);

	/**
	 * @return true if the item is a valid germling (plantable sapling, seed, etc.) for this type.
	 */
	boolean isGermling(ItemStack itemstack);

	/**
	 * @return true if the item is something that can drop from this type without actually being harvested as a crop. (Apples or sapling from decaying leaves.)
	 */
	boolean isWindfall(ItemStack itemstack);

	/**
	 * Plants a sapling by manipulating the world. The {@link IFarmLogic} should have verified the given location as valid. Called by the {@link IFarmHousing}
	 * which handles resources.
	 *
	 * @return true on success, false otherwise.
	 */
	boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos);

}
