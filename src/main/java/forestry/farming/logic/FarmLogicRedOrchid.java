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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.core.utils.BlockUtil;
import forestry.plugins.PluginExtraUtilities;

public class FarmLogicRedOrchid extends FarmLogicHomogeneous {

	public FarmLogicRedOrchid(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public String getUnlocalizedName() {
		return "for.farm.orchid";
	}

	@Override
	public ItemStack getIconItemStack() {
		return PluginExtraUtilities.orchidStack;
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
	protected boolean maintainGermlings(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			if (!world.isBlockLoaded(position)) {
				break;
			}

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
