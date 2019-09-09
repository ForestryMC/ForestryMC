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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmableInfo;
import forestry.core.utils.BlockUtil;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableVanillaMushroom extends FarmableBase {
	private final Block hugeMushroomBlock;

	public FarmableVanillaMushroom(ItemStack mushroom, BlockState plantedMushroom, Block hugeMushroomBlock) {
		super(mushroom, plantedMushroom, hugeMushroomBlock.getDefaultState(), false);
		this.hugeMushroomBlock = hugeMushroomBlock;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, BlockState blockState) {
		if (blockState.getBlock() != hugeMushroomBlock) {
			return null;
		}

		return new CropDestroy(world, blockState, pos, null);
	}

	@Override
	public boolean plantSaplingAt(PlayerEntity player, ItemStack germling, World world, BlockPos pos) {
		if (!plantedState.isValidPosition(world, pos)) {
			return false;
		}
		return BlockUtil.setBlockWithPlaceSound(world, pos, plantedState);
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		info.addProducts(germling);
	}
}
