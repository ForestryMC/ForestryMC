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

import java.util.Collection;
import java.util.Stack;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;
import forestry.farming.FarmRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmLogicEnder extends FarmLogicHomogeneous {

	public FarmLogicEnder() {
		super(new ItemStack(Blocks.END_STONE), Blocks.END_STONE.getDefaultState(), FarmRegistry.getInstance().getFarmables("farmEnder"));
	}

	@Override
	public String getName() {
		return "Managed Ender Farm";
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Items.ENDER_EYE);
	}

	@Override
	public int getFertilizerConsumption() {
		return 20;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return 0;
	}

	@Override
	public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
		return NonNullList.create();
	}

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
		Stack<ICrop> crops = new Stack<>();
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos.up(), direction, i);
			IBlockState blockState = world.getBlockState(position);
			for (IFarmable farmable : farmables) {
				ICrop crop = farmable.getCropAt(world, position, blockState);
				if (crop != null) {
					crops.push(crop);
					break;
				}
			}

		}
		return crops;

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
			if (!isAcceptedSoil(blockState)) {
				continue;
			}

			return trySetCrop(world, farmHousing, position);
		}

		return false;
	}

	private boolean trySetCrop(World world, IFarmHousing farmHousing, BlockPos position) {
		for (IFarmable candidate : farmables) {
			if (farmHousing.plantGermling(candidate, world, position)) {
				return true;
			}
		}

		return false;
	}

}
