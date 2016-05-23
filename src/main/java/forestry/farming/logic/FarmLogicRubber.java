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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.plugins.compat.PluginIC2;

public class FarmLogicRubber extends FarmLogic {

	private boolean inActive;

	public FarmLogicRubber() {
		if (PluginIC2.rubberWood == null || PluginIC2.resin == null) {
			Log.warning("Failed to init a farm logic %s since IC2 was not found", getClass().getName());
			inActive = true;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem() {
		if (!inActive) {
			return PluginIC2.resin.getItem();
		} else {
			return Items.GUNPOWDER;
		}
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
	public Collection<ItemStack> collect(World world, IFarmHousing farmHousing) {
		return null;
	}

	@Override
	public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		return false;
	}

	private final Map<BlockPos, Integer> lastExtents = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
		if (inActive) {
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
		Set<BlockPos> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		// Determine what type we want to harvest.
		IBlockState blockState = world.getBlockState(position);
		Block block = blockState.getBlock();
		if (!ItemStackUtil.equals(block, PluginIC2.rubberWood)) {
			return crops;
		}

		int meta = block.getMetaFromState(blockState);
		if (meta >= 2 && meta <= 5) {
			crops.push(new CropDestroy(world, blockState, position, null));
		}

		List<BlockPos> candidates = processHarvestBlock(world, crops, seen, position);
		List<BlockPos> temp = new ArrayList<>();
		while (!candidates.isEmpty() && crops.size() < 100) {
			for (BlockPos candidate : candidates) {
				temp.addAll(processHarvestBlock(world, crops, seen, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}

		return crops;
	}

	private List<BlockPos> processHarvestBlock(World world, Stack<ICrop> crops, Set<BlockPos> seen, BlockPos position) {
		List<BlockPos> candidates = new ArrayList<>();

		// Get additional candidates to return
		for (int j = 0; j < 2; j++) {
			BlockPos candidate = position.add(0, j, 0);
			if (candidate.equals(position)) {
				continue;
			}

			// See whether the given position has already been processed
			if (seen.contains(candidate)) {
				continue;
			}

			IBlockState blockState = world.getBlockState(candidate);
			Block block = blockState.getBlock();
			if (ItemStackUtil.equals(block, PluginIC2.rubberWood)) {
				int meta = block.getMetaFromState(blockState);
				if (meta >= 2 && meta <= 5) {
					crops.push(new CropRubber(world, blockState, candidate));
				}
				candidates.add(candidate);
				seen.add(candidate);
			}
		}

		return candidates;
	}

}
