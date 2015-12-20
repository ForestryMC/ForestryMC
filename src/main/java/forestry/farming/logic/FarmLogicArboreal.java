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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.blocks.BlockSoil;
import forestry.core.utils.BlockPosUtil;
import forestry.plugins.PluginCore;

public class FarmLogicArboreal extends FarmLogicHomogeneous {

	public FarmLogicArboreal(IFarmHousing housing, ItemStack resource, ItemStack ground, Iterable<IFarmable> germlings) {
		super(housing, resource, ground, germlings);
	}

	public FarmLogicArboreal(IFarmHousing housing) {
		super(housing, new ItemStack(Blocks.dirt), PluginCore.blocks.soil.get(BlockSoil.SoilType.HUMUS, 1), Farmables.farmables.get("farmArboreal"));
	}

	@Override
	public boolean isAcceptedSoil(ItemStack soil) {
		if (super.isAcceptedSoil(soil)) {
			return true;
		}

		Block block = Block.getBlockFromItem(soil.getItem());
		if (!(block instanceof BlockSoil)) {
			return false;
		}
		BlockSoil blockSoil = (BlockSoil) block;
		return blockSoil.getTypeFromMeta(soil.getItemDamage()) == BlockSoil.SoilType.HUMUS;
	}

	@Override
	public String getName() {
		return "Managed Arboretum";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getIconItem() {
		return Item.getItemFromBlock(Blocks.sapling);
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (10 * hydrationModifier);
	}

	@Override
	public Collection<ItemStack> collect() {
		Collection<ItemStack> products = produce;
		produce = collectEntityItems(true);
		return products;
	}

	private final HashMap<BlockPos, Integer> lastExtentsHarvest = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, FarmDirection direction, int extent) {

		BlockPos start = new BlockPos(x, y, z);
		if (!lastExtentsHarvest.containsKey(start)) {
			lastExtentsHarvest.put(start, 0);
		}

		int lastExtent = lastExtentsHarvest.get(start);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		BlockPos position = translateWithOffset(x, y + 1, z, direction, lastExtent);
		Collection<ICrop> crops = getHarvestBlocks(position);
		lastExtent++;
		lastExtentsHarvest.put(start, lastExtent);

		return crops;
	}

	private Collection<ICrop> getHarvestBlocks(BlockPos position) {

		World world = getWorld();

		Set<BlockPos> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		// Determine what type we want to harvest.
		IFarmable germling = null;
		for (IFarmable germl : germlings) {
			ICrop crop = germl.getCropAt(world, position);
			if (crop == null) {
				continue;
			}

			crops.push(crop);
			seen.add(position);
			germling = germl;
			break;
		}

		if (germling == null) {
			return crops;
		}

		ArrayList<BlockPos> candidates = processHarvestBlock(germling, crops, seen, position, position);
		ArrayList<BlockPos> temp = new ArrayList<>();
		while (!candidates.isEmpty()) {
			for (BlockPos candidate : candidates) {
				temp.addAll(processHarvestBlock(germling, crops, seen, position, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}

		return crops;
	}

	private ArrayList<BlockPos> processHarvestBlock(IFarmable germling, Stack<ICrop> crops, Set<BlockPos> seen, BlockPos start, BlockPos position) {

		World world = getWorld();

		ArrayList<BlockPos> candidates = new ArrayList<>();

		// Get additional candidates to return
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				for (int z = -1; z < 2; z++) {
					BlockPos candidate = position.add(x, y, z);
					if (candidate.equals(position)) {
						continue;
					}
					if (Math.abs(candidate.getX() - start.getX()) > 10) {
						continue;
					}
					if (Math.abs(candidate.getZ() - start.getZ()) > 10) {
						continue;
					}

					// See whether the given position has already been processed
					if (seen.contains(candidate)) {
						continue;
					}

					ICrop crop = germling.getCropAt(world, candidate);
					if (crop != null) {
						crops.push(crop);
						candidates.add(candidate);
						seen.add(candidate);
					}
				}
			}
		}

		return candidates;
	}

	@Override
	protected boolean maintainGermlings(int x, int ySaplings, int z, FarmDirection direction, int extent) {

		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(x, ySaplings, z, direction, i);

			if (BlockPosUtil.isAirBlock(world, position)) {
				BlockPos soilBelowPosition = new BlockPos(position.getX(), position.getY() - 1, position.getZ());
				ItemStack soilBelow = BlockPosUtil.getAsItemStack(world, soilBelowPosition);
				if (isAcceptedSoil(soilBelow)) {
					return plantSapling(position);
				}
			}
		}
		return false;
	}

	private boolean plantSapling(BlockPos position) {
		World world = getWorld();
		Collections.shuffle(germlings);
		for (IFarmable candidate : germlings) {
			if (housing.plantGermling(candidate, world, position)) {
				return true;
			}
		}

		return false;
	}

}
