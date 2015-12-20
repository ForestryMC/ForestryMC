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
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.utils.BlockPosUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.plugins.compat.deprecated.PluginIC2;

public class FarmLogicRubber extends FarmLogic {

	private boolean inActive;

	public FarmLogicRubber(IFarmHousing housing) {
		super(housing);
		if (PluginIC2.rubberwood == null || PluginIC2.resin == null) {
			Log.warning("Failed to init a farm logic %s since IC2 was not found", getClass().getName());
			inActive = true;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getIconItem() {
		if (!inActive) {
			return PluginIC2.resin.getItem();
		} else {
			return Items.gunpowder;
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
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean cultivate(BlockPos pos, FarmDirection direction, int extent) {
		return false;
	}

	private final HashMap<BlockPos, Integer> lastExtents = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, FarmDirection direction, int extent) {
		if (inActive) {
			return null;
		}

		BlockPos start = new BlockPos(x, y, z);
		if (!lastExtents.containsKey(start)) {
			lastExtents.put(start, 0);
		}

		int lastExtent = lastExtents.get(start);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		BlockPos position = translateWithOffset(x, y + 1, z, direction, lastExtent);
		Collection<ICrop> crops = getHarvestBlocks(position);
		lastExtent++;
		lastExtents.put(start, lastExtent);

		return crops;
	}

	private Collection<ICrop> getHarvestBlocks(BlockPos position) {

		Set<BlockPos> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		World world = getWorld();

		// Determine what type we want to harvest.
		Block block = BlockPosUtil.getBlock(world, position);
		if (!ItemStackUtil.equals(block, PluginIC2.rubberwood)) {
			return crops;
		}

		int meta = BlockPosUtil.getBlockMeta(world, position);
		if (meta >= 2 && meta <= 5) {
			crops.push(new CropRubber(getWorld(), block, meta, position));
		}

		ArrayList<BlockPos> candidates = processHarvestBlock(crops, seen, position);
		ArrayList<BlockPos> temp = new ArrayList<>();
		while (!candidates.isEmpty() && crops.size() < 100) {
			for (BlockPos candidate : candidates) {
				temp.addAll(processHarvestBlock(crops, seen, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}

		return crops;
	}

	private ArrayList<BlockPos> processHarvestBlock(Stack<ICrop> crops, Set<BlockPos> seen, BlockPos position) {
		World world = getWorld();

		ArrayList<BlockPos> candidates = new ArrayList<>();

		// Get additional candidates to return
		for (int j = 0; j < 2; j++) {
			BlockPos candidate = new BlockPos(position);
			if (candidate.equals(position)) {
				continue;
			}

			// See whether the given position has already been processed
			if (seen.contains(candidate)) {
				continue;
			}

			Block block = BlockPosUtil.getBlock(world, candidate);
			if (ItemStackUtil.equals(block, PluginIC2.rubberwood)) {
				int meta = BlockPosUtil.getBlockMeta(world, candidate);
				if (meta >= 2 && meta <= 5) {
					crops.push(new CropRubber(world, block, meta, candidate));
				}
				candidates.add(candidate);
				seen.add(candidate);
			}
		}

		return candidates;
	}

}
