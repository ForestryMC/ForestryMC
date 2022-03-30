/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.logic.farmables;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmableInfo;
import forestry.core.utils.BlockUtil;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableVanillaMushroom extends FarmableBase {
	private final Block hugeMushroomBlock;

	public FarmableVanillaMushroom(ItemStack mushroom, BlockState plantedMushroom, Block hugeMushroomBlock) {
		super(mushroom, plantedMushroom, hugeMushroomBlock.defaultBlockState(), false);
		this.hugeMushroomBlock = hugeMushroomBlock;
	}

	@Override
	public ICrop getCropAt(Level world, BlockPos pos, BlockState blockState) {
		if (blockState.getBlock() != hugeMushroomBlock) {
			return null;
		}

		return new CropDestroy(world, blockState, pos, null);
	}

	@Override
	public boolean plantSaplingAt(Player player, ItemStack germling, Level world, BlockPos pos) {
		if (!plantedState.canSurvive(world, pos)) {
			return false;
		}
		return BlockUtil.setBlockWithPlaceSound(world, pos, plantedState);
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		info.addProducts(germling);
	}
}
