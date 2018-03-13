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

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.api.genetics.IFruitBearer;
import forestry.core.ModuleCore;
import forestry.core.tiles.TileUtil;
import forestry.farming.logic.crops.CropFruit;

public class FarmLogicOrchard extends FarmLogic {

	private final HashMap<BlockPos, Integer> lastExtents = new HashMap<>();
	private final ImmutableList<Block> traversalBlocks;

	public FarmLogicOrchard(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);

		ImmutableList.Builder<Block> traversalBlocksBuilder = ImmutableList.builder();
		//		if (ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.AGRICRAFT) || ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.INDUSTRIALCRAFT)) {
		//			traversalBlocksBuilder.add(Blocks.FARMLAND);
		//		}
		//		if (ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.INDUSTRIALCRAFT)) {
		//			traversalBlocksBuilder.add(Blocks.DIRT);
		//		}
		//		if (ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.PLANTMEGAPACK)) {
		//			traversalBlocksBuilder.add(Blocks.WATER);
		//		}
		//
		//		{
		//			Block grapeVine = GameRegistry.findBlock("Growthcraft|Grapes", "grc.grapeVine1");
		//			if (grapeVine != null) {
		//				traversalBlocksBuilder.add(grapeVine);
		//			}
		//		}
		this.traversalBlocks = traversalBlocksBuilder.build();
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (40 * hydrationModifier);
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

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
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

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(ModuleCore.getItems().fruits);
	}

	@Override
	public String getUnlocalizedName() {
		return "for.farm.orchard";
	}

	private Collection<ICrop> getHarvestBlocks(World world, BlockPos position) {
		Set<BlockPos> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		if (!world.isBlockLoaded(position)) {
			return Collections.emptyList();
		}

		// Determine what type we want to harvest.
		IBlockState blockState = world.getBlockState(position);
		Block block = blockState.getBlock();
		if (!block.isWood(world, position) && !isBlockTraversable(blockState, traversalBlocks) && !isFruitBearer(world, position, blockState)) {
			return crops;
		}

		List<BlockPos> candidates = processHarvestBlock(world, crops, seen, position, position);
		List<BlockPos> temp = new ArrayList<>();
		while (!candidates.isEmpty() && crops.size() < 20) {
			for (BlockPos candidate : candidates) {
				temp.addAll(processHarvestBlock(world, crops, seen, position, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}

		return crops;
	}

	private List<BlockPos> processHarvestBlock(World world, Stack<ICrop> crops, Set<BlockPos> seen, BlockPos start, BlockPos position) {
		List<BlockPos> candidates = new ArrayList<>();

		// Get additional candidates to return
		for (int i = -2; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					BlockPos candidate = position.add(i, j, k);
					if (Math.abs(candidate.getX() - start.getX()) > 5) {
						continue;
					}
					if (Math.abs(candidate.getZ() - start.getZ()) > 5) {
						continue;
					}

					// See whether the given position has already been processed
					if (seen.contains(candidate)) {
						continue;
					}
					if (!world.isBlockLoaded(candidate) || world.isAirBlock(candidate)) {
						continue;
					}

					IBlockState blockState = world.getBlockState(candidate);
					Block block = blockState.getBlock();
					if (block.isWood(world, candidate) || isBlockTraversable(blockState, traversalBlocks)) {
						candidates.add(candidate);
						seen.add(candidate);
					}
					if (isFruitBearer(world, candidate, blockState)) {
						candidates.add(candidate);
						seen.add(candidate);

						ICrop crop = getCropAt(world, candidate);
						if (crop != null) {
							crops.push(crop);
						}
					}
				}
			}
		}

		return candidates;
	}

	private boolean isFruitBearer(World world, BlockPos position, IBlockState blockState) {
		IFruitBearer tile = TileUtil.getTile(world, position, IFruitBearer.class);
		if (tile != null) {
			return true;
		}

		for (IFarmable farmable : getFarmables()) {
			if (farmable.isSaplingAt(world, position, blockState)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isBlockTraversable(IBlockState blockState, ImmutableList<Block> traversalBlocks) {
		Block candidate = blockState.getBlock();
		for (Block block : traversalBlocks) {
			if (block == candidate) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	private ICrop getCropAt(World world, BlockPos position) {
		IFruitBearer fruitBearer = TileUtil.getTile(world, position, IFruitBearer.class);

		if (fruitBearer != null) {
			if (fruitBearer.hasFruit() && fruitBearer.getRipeness() >= 0.9f) {
				return new CropFruit(world, position);
			}
		} else {
			return getCrop(world, position);
		}
		return null;
	}

}
