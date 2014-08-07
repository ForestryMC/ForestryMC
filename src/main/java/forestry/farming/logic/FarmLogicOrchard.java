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
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.genetics.IFruitBearer;
import forestry.core.config.ForestryItem;
import forestry.core.utils.Vect;

public class FarmLogicOrchard extends FarmLogic {

	public FarmLogicOrchard(IFarmHousing housing) {
		super(housing);
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

		world = housing.getWorld();

		Collection<ICrop> crops = null;

		Vect start = new Vect(x, y, z);
		if (!lastExtents.containsKey(start))
			lastExtents.put(start, 0);

		int lastExtent = lastExtents.get(start);
		if (lastExtent > extent)
			lastExtent = 0;

		// Proxies.log.finest("Logic %s is searching in direction %s at %s/%s/%s with extension %s.", getClass(), direction, x, y, z, lastExtent);

		Vect position = translateWithOffset(x, y + 1, z, direction, lastExtent);
		crops = getHarvestBlocks(position);
		lastExtent++;
		lastExtents.put(start, lastExtent);

		return crops;
	}

	private Collection<ICrop> getHarvestBlocks(Vect position) {

		ArrayList<Vect> seen = new ArrayList<Vect>();
		Stack<ICrop> crops = new Stack<ICrop>();

		// Determine what type we want to harvest.
		IFruitBearer bearer = getFruitBlock(position);
		Block block = getBlock(position);
		if ((!block.isWood(world, position.x, position.y, position.z)) && bearer == null)
			return crops;

		ArrayList<Vect> candidates = processHarvestBlock(crops, seen, position, position);
		ArrayList<Vect> temp = new ArrayList<Vect>();
		while (!candidates.isEmpty() && crops.size() < 20) {
			for (Vect candidate : candidates)
				temp.addAll(processHarvestBlock(crops, seen, position, candidate));
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}
		// Proxies.log.finest("Logic at %s/%s/%s has seen %s blocks.", position.x, position.y, position.z, seen.size());

		return crops;
	}

	private ArrayList<Vect> processHarvestBlock(Stack<ICrop> crops, Collection<Vect> seen, Vect start, Vect position) {

		ArrayList<Vect> candidates = new ArrayList<Vect>();

		// Get additional candidates to return
		for (int i = -1; i < 2; i++)
			for (int j = 0; j < 2; j++)
				for (int k = -1; k < 2; k++) {
					Vect candidate = new Vect(position.x + i, position.y + j, position.z + k);
					if (candidate.equals(position))
						continue;
					if (Math.abs(candidate.x - start.x) > 5)
						continue;
					if (Math.abs(candidate.z - start.z) > 5)
						continue;

					// See whether the given position has already been processed
					boolean skip = false;
					for (Vect prcs : seen)
						if (candidate.equals(prcs)) {
							skip = true;
							break;
						}

					if (skip)
						continue;

					IFruitBearer bearer = getFruitBlock(candidate);
					if (bearer != null && bearer.hasFruit()) {
						if (bearer.getRipeness() >= 0.9f)
							crops.push(new CropFruit(world, candidate, bearer.getFruitFamily()));
						candidates.add(candidate);
						seen.add(candidate);
					} else if (this.isWoodBlock(candidate)) {
						candidates.add(candidate);
						seen.add(candidate);
					}
				}

		return candidates;
	}

	private IFruitBearer getFruitBlock(Vect position) {
		TileEntity tile = world.getTileEntity(position.x, position.y, position.z);
		if (!(tile instanceof IFruitBearer))
			return null;

		return (IFruitBearer) tile;
	}

}
