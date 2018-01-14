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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmInstance;
import forestry.core.utils.BlockUtil;

public class FarmLogicExU extends FarmLogicHomogeneous {

	private final String name;
	private final Item iconItem;

	public FarmLogicExU(IFarmInstance instance, boolean isManual, String name, Item iconItem) {
		super(instance, isManual);
		this.name = name;
		this.iconItem = iconItem;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(iconItem);
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

			return trySetCrop(world, farmHousing, position, direction);
		}

		return false;
	}

}
