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
package forestry.farming.logic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.core.utils.BlockUtil;

public class FarmLogicSucculent extends FarmLogicHomogeneous {
	public FarmLogicSucculent(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Items.DYE, 1, 2);
	}

	@Override
	public String getUnlocalizedName() {
		return "for.farm.succulent";
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return 1;
	}

	@Override
	protected boolean maintainGermlings(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			IBlockState state = world.getBlockState(position);
			if (!world.isAirBlock(position) && !BlockUtil.isReplaceableBlock(state, world, position)) {
				continue;
			}

			BlockPos soilPos = position.down();
			IBlockState blockState = world.getBlockState(soilPos);
			if (!isAcceptedSoil(blockState) || !canPlace(world, position)) {
				continue;
			}

			if (trySetCrop(world, farmHousing, position, direction)) {
				return true;
			}
		}

		return false;
	}

	private boolean canPlace(World world, BlockPos position) {
		return Blocks.CACTUS.canBlockStay(world, position);
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack stack) {
		return false;
	}

}
