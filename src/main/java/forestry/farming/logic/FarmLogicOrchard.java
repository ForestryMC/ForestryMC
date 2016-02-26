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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.api.genetics.IFruitBearer;
import forestry.core.utils.vect.Vect;
import forestry.core.utils.vect.VectUtil;
import forestry.plugins.PluginCore;

public class FarmLogicOrchard extends FarmLogic {

	private final Collection<IFarmable> farmables;
	private final HashMap<Vect, Integer> lastExtents = new HashMap<>();
	private final ImmutableList<Block> traversalBlocks;

	public FarmLogicOrchard(IFarmHousing housing) {
		super(housing);
		this.farmables = Farmables.farmables.get("farmOrchard");

		ImmutableList.Builder<Block> traversalBlocksBuilder = ImmutableList.builder();
		//		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.AGRICRAFT) || ForestryAPI.enabledPlugins.contains(ForestryPluginUids.INDUSTRIALCRAFT)) {
		//			traversalBlocksBuilder.add(Blocks.farmland);
		//		}
		//		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.INDUSTRIALCRAFT)) {
		//			traversalBlocksBuilder.add(Blocks.dirt);
		//		}
		//		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.PLANTMEGAPACK)) {
		//			traversalBlocksBuilder.add(Blocks.water);
		//		}
		//
		//		{
		//			Block grapeVine = GameRegistry.findBlock("Growthcraft|Grapes", "grc.grapeVine1");
		//			if (grapeVine != null) {
		//				traversalBlocksBuilder.add(grapeVine);
		//			}
		//		}

		traversalBlocksBuilder.build();
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
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean cultivate(BlockPos pos, FarmDirection direction, int extent) {
		return false;
	}

	@Override
	public Collection<ICrop> harvest(BlockPos pos, FarmDirection direction, int extent) {

		Vect start = new Vect(pos);
		if (!lastExtents.containsKey(start)) {
			lastExtents.put(start, 0);
		}

		int lastExtent = lastExtents.get(start);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		Vect position = translateWithOffset(pos.add(0, 1, 0), direction, lastExtent);
		Collection<ICrop> crops = getHarvestBlocks(position);
		lastExtent++;
		lastExtents.put(start, lastExtent);

		return crops;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem() {
		return PluginCore.items.fruits;
	}

	@Override
	public String getName() {
		return "Orchard";
	}

	private Collection<ICrop> getHarvestBlocks(Vect position) {

		Set<Vect> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		World world = getWorld();

		// Determine what type we want to harvest.
		if (!VectUtil.isWoodBlock(world, position) && !isBlockTraversable(world, position, traversalBlocks) && !isFruitBearer(world, position)) {
			return crops;
		}

		List<Vect> candidates = processHarvestBlock(crops, seen, position, position);
		List<Vect> temp = new ArrayList<>();
		while (!candidates.isEmpty() && crops.size() < 20) {
			for (Vect candidate : candidates) {
				temp.addAll(processHarvestBlock(crops, seen, position, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}

		return crops;
	}

	private List<Vect> processHarvestBlock(Stack<ICrop> crops, Set<Vect> seen, Vect start, Vect position) {
		World world = getWorld();

		List<Vect> candidates = new ArrayList<>();

		// Get additional candidates to return
		for (int i = -2; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					Vect candidate = position.add(i, j, k);
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
					if (VectUtil.isAirBlock(world, candidate)) {
						continue;
					}
					if (VectUtil.isWoodBlock(world, candidate) || isBlockTraversable(world, candidate, traversalBlocks)) {
						candidates.add(candidate);
						seen.add(candidate);
					} else if (isFruitBearer(world, candidate)) {
						candidates.add(candidate);
						seen.add(candidate);

						ICrop crop = getCrop(world, candidate);
						if (crop != null) {
							crops.push(crop);
						}
					}
				}
			}
		}

		return candidates;
	}

	private boolean isFruitBearer(World world, Vect position) {

		TileEntity tile = world.getTileEntity(position);
		if (tile instanceof IFruitBearer) {
			return true;
		}

		for (IFarmable farmable : farmables) {
			if (farmable.isSaplingAt(world, position)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isBlockTraversable(World world, Vect position, ImmutableList<Block> traversalBlocks) {

		Block candidate = VectUtil.getBlock(world, position);
		for (Block block : traversalBlocks) {
			if (block == (candidate)) {
				return true;
			}
		}
		return false;
	}

	private ICrop getCrop(World world, Vect position) {

		TileEntity tile = world.getTileEntity(position);

		if (tile instanceof IFruitBearer) {
			IFruitBearer fruitBearer = (IFruitBearer) tile;
			if (fruitBearer.hasFruit() && fruitBearer.getRipeness() >= 0.9f) {
				return new CropFruit(world, position);
			}
		} else {
			for (IFarmable seed : farmables) {
				ICrop crop = seed.getCropAt(world, position);
				if (crop != null) {
					return crop;
				}
			}
		}
		return null;
	}

}
