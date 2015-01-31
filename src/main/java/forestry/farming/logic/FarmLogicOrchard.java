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
import java.util.Set;
import java.util.Stack;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.api.genetics.IFruitBearer;
import forestry.core.config.ForestryItem;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;

public class FarmLogicOrchard extends FarmLogic {

	private final Collection<IFarmable> farmables;

	public FarmLogicOrchard(IFarmHousing housing) {
		super(housing);
		this.farmables = Farmables.farmables.get("farmOrchard");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return ForestryItem.fruits.item().getIconFromDamage(0);
	}

	@Override
	public String getName() {
		return "Orchard";
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
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent) {
		return false;
	}

	private final HashMap<Vect, Integer> lastExtents = new HashMap<Vect, Integer>();

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {

		Vect start = new Vect(x, y, z);
		if (!lastExtents.containsKey(start)) {
			lastExtents.put(start, 0);
		}

		int lastExtent = lastExtents.get(start);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		Vect position = translateWithOffset(x, y + 1, z, direction, lastExtent);
		Collection<ICrop> crops = getHarvestBlocks(position);
		lastExtent++;
		lastExtents.put(start, lastExtent);

		return crops;
	}

	private Collection<ICrop> getHarvestBlocks(Vect position) {

		Set<Vect> seen = new HashSet<Vect>();
		Stack<ICrop> crops = new Stack<ICrop>();

		World world = getWorld();

		// Determine what type we want to harvest.
		if (!VectUtil.isWoodBlock(world, position) && !isFruitBearer(world, position)) {
			return crops;
		}

		List<Vect> candidates = processHarvestBlock(crops, seen, position, position);
		List<Vect> temp = new ArrayList<Vect>();
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

		List<Vect> candidates = new ArrayList<Vect>();

		// Get additional candidates to return
		for (int i = -1; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					Vect candidate = new Vect(position.x + i, position.y + j, position.z + k);
					if (candidate.equals(position)) {
						continue;
					}
					if (Math.abs(candidate.x - start.x) > 5) {
						continue;
					}
					if (Math.abs(candidate.z - start.z) > 5) {
						continue;
					}

					// See whether the given position has already been processed
					if (seen.contains(candidate)) {
						continue;
					}

					if (VectUtil.isWoodBlock(world, candidate)) {
						candidates.add(candidate);
						seen.add(candidate);
					} else if (isFruitBearer(world, candidate)){
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

		if (VectUtil.isAirBlock(world, position)) {
			return false;
		}

		TileEntity tile = world.getTileEntity(position.x, position.y, position.z);
		if (tile instanceof IFruitBearer) {
			return true;
		}

		for (IFarmable farmable : farmables) {
			if (farmable.isSaplingAt(world, position.x, position.y, position.z)) {
				return true;
			}
		}

		return false;
	}

	private ICrop getCrop(World world, Vect position) {

		if (VectUtil.isAirBlock(world, position)) {
			return null;
		}

		TileEntity tile = world.getTileEntity(position.x, position.y, position.z);

		if (tile instanceof IFruitBearer) {
			IFruitBearer fruitBearer = (IFruitBearer) tile;
			if (fruitBearer.hasFruit() && fruitBearer.getRipeness() >= 0.9f) {
				return new CropFruit(world, position, fruitBearer.getFruitFamily());
			}
		} else {
			for (IFarmable seed : farmables) {
				ICrop crop = seed.getCropAt(world, position.x, position.y, position.z);
				if (crop != null) {
					return crop;
				}
			}
		}
		return null;
	}

}
