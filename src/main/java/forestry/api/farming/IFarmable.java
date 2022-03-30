/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * IFarmable describes a crop or other harvestable object and can be used to inspect item stacks and blocks for matches.
 */
public interface IFarmable {

	/**
	 * @return true if the block at the given location is a "sapling" for this type, i.e. a non-harvestable immature version of the crop.
	 */
	boolean isSaplingAt(Level world, BlockPos pos, BlockState blockState);

	/**
	 * @return {@link ICrop} if the block at the given location is a harvestable and mature crop, null otherwise.
	 */
	@Nullable
	ICrop getCropAt(Level world, BlockPos pos, BlockState blockState);

	/**
	 * @return true if the item is a valid germling (plantable sapling, seed, etc.) for this type.
	 */
	boolean isGermling(ItemStack itemstack);

	default void addInformation(IFarmableInfo info) {
	}

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
	boolean plantSaplingAt(Player player, ItemStack germling, Level world, BlockPos pos);

}
