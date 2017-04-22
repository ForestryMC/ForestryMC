/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.plugins.compat.PluginIC2;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmLogicRubber extends FarmLogic {

	private boolean active = true;

	public FarmLogicRubber() {
		if (PluginIC2.rubberWood == null || PluginIC2.resin == null) {
			Log.warning("Failed to init a farm logic {} since IC2 rubber wood or resin were not found", getClass().getName());
			active = false;
		}
	}

	@Override
	public ItemStack getIconItemStack() {
		return PluginIC2.resin;
	}

	@Override
	public String getName() {
		return "Rubber Plantation";
	}

	@Override
	public int getFertilizerConsumption() {
		return 40;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (5 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack stack) {
		return false;
	}

	@Override
	public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
		return NonNullList.create();
	}

	@Override
	public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		return false;
	}

	private final Map<BlockPos, Integer> lastExtents = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
		if (!active) {
			return null;
		}

		if (!lastExtents.containsKey(pos)) {
			lastExtents.put(pos, 0);
		}

		int lastExtent = lastExtents.get(pos);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		BlockPos position = translateWithOffset(pos.up(), direction, lastExtent);
		Collection<ICrop> crops = getHarvestBlocks(world, position);
		lastExtent++;
		lastExtents.put(pos, lastExtent);

		return crops;
	}

	private Collection<ICrop> getHarvestBlocks(World world, BlockPos position) {
		Stack<ICrop> crops = new Stack<>();

		for (int j = 0; j < 10; j++) {
			BlockPos candidate = position.add(0, j, 0);

			IBlockState blockState = world.getBlockState(candidate);
			Block block = blockState.getBlock();
			if (!ItemStackUtil.equals(block, PluginIC2.rubberWood)) {
				break;
			}

			if (CropRubber.hasRubberToHarvest(blockState)) {
				crops.push(new CropRubber(world, blockState, candidate));
				break;
			}
		}

		return crops;
	}

}
