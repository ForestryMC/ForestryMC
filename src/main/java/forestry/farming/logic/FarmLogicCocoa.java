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
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.vect.MutableVect;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;

public class FarmLogicCocoa extends FarmLogic {

	private final IFarmable cocoa = new FarmableCocoa();

	public FarmLogicCocoa(IFarmHousing housing) {
		super(housing);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getIconItem() {
		return Items.dye;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconMetadata() {
		return 3;
	}

	@Override
	public String getName() {
		return "Cocoa Plantation";
	}

	@Override
	public int getFertilizerConsumption() {
		return 120;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (20 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return cocoa.isGermling(itemstack);
	}

	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	private final HashMap<Vect, Integer> lastExtentsCultivation = new HashMap<Vect, Integer>();

	@Override
	public boolean cultivate(BlockPos pos, FarmDirection direction, int extent) {

		Vect start = new Vect(pos);
		if (!lastExtentsCultivation.containsKey(start)) {
			lastExtentsCultivation.put(start, 0);
		}

		int lastExtent = lastExtentsCultivation.get(start);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		Vect position = translateWithOffset(pos, direction, lastExtent);
		boolean result = tryPlantingCocoa(position);

		lastExtent++;
		lastExtentsCultivation.put(start, lastExtent);

		return result;
	}

	private final HashMap<Vect, Integer> lastExtentsHarvest = new HashMap<Vect, Integer>();

	@Override
	public Collection<ICrop> harvest(BlockPos pos, FarmDirection direction, int extent) {

		Vect start = new Vect(pos);
		if (!lastExtentsHarvest.containsKey(start)) {
			lastExtentsHarvest.put(start, 0);
		}

		int lastExtent = lastExtentsHarvest.get(start);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		Vect position = translateWithOffset(pos, direction, lastExtent);
		Collection<ICrop> crops = getHarvestBlocks(position);
		lastExtent++;
		lastExtentsHarvest.put(start, lastExtent);

		return crops;
	}

	private boolean tryPlantingCocoa(Vect position) {

		World world = getWorld();

		MutableVect current = new MutableVect(position);
		while (VectUtil.isWoodBlock(world, current) && (VectUtil.getBlockMeta(world, current) & 3) == 3) {

			for (EnumFacing direction : EnumFacing.values()) {
				if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
					continue;
				}

				Vect candidate = new Vect(current.getX() + direction.getFrontOffsetX(), current.getY(),
						current.getZ() + direction.getFrontOffsetZ());
				if (VectUtil.isAirBlock(world, candidate)) {
					return housing.plantGermling(cocoa, world, current.getPos());
				}
			}

			current.setY(1 + current.getY());
			if (current.getY() - position.getY() > 1) {
				break;
			}
		}

		return false;
	}

	private Collection<ICrop> getHarvestBlocks(Vect position) {

		Set<Vect> seen = new HashSet<Vect>();
		Stack<ICrop> crops = new Stack<ICrop>();

		// Determine what type we want to harvest.
		Block block = VectUtil.getBlock(getWorld(), position);

		ICrop crop = null;
		if (!block.isWood(getWorld(), position.getPos())) {
			crop = cocoa.getCropAt(getWorld(), position.getPos());
			if (crop == null) {
				return crops;
			}
		}

		if (crop != null) {
			crops.add(crop);
		}

		ArrayList<Vect> candidates = processHarvestBlock(crops, seen, position, position);
		ArrayList<Vect> temp = new ArrayList<Vect>();
		while (!candidates.isEmpty() && crops.size() < 20) {
			for (Vect candidate : candidates) {
				temp.addAll(processHarvestBlock(crops, seen, position, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}
		// Proxies.log.finest("Logic %s at %s/%s/%s has seen %s blocks.",
		// getClass().getName(), position.x, position.y, position.z,
		// seen.size());

		return crops;
	}

	private ArrayList<Vect> processHarvestBlock(Stack<ICrop> crops, Set<Vect> seen, Vect start, Vect position) {

		World world = getWorld();

		ArrayList<Vect> candidates = new ArrayList<Vect>();

		// Get additional candidates to return
		for (int i = -1; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					Vect candidate = position.add(i, j, k);
					if (candidate.equals(position)) {
						continue;
					}
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

					ICrop crop = cocoa.getCropAt(world, candidate.getPos());
					if (crop != null) {
						crops.push(crop);
						candidates.add(candidate);
						seen.add(candidate);
					} else if (VectUtil.isWoodBlock(world, candidate)) {
						candidates.add(candidate);
						seen.add(candidate);
					}
				}
			}
		}

		return candidates;
	}

}
