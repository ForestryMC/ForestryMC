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
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.vect.Vect;
import forestry.core.utils.vect.VectUtil;
import forestry.plugins.compat.PluginIC2;

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
	public IIcon getIcon() {
		if (!inActive) {
			return PluginIC2.resin.getIconIndex();
		} else {
			return Items.gunpowder.getIconFromDamage(0);
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
	public boolean cultivate(int x, int y, int z, FarmDirection direction, int extent) {
		return false;
	}

	private final HashMap<Vect, Integer> lastExtents = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, FarmDirection direction, int extent) {
		if (inActive) {
			return null;
		}

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

		Set<Vect> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		World world = getWorld();

		// Determine what type we want to harvest.
		Block block = VectUtil.getBlock(world, position);
		if (!ItemStackUtil.equals(block, PluginIC2.rubberwood)) {
			return crops;
		}

		int meta = VectUtil.getBlockMeta(world, position);
		if (meta >= 2 && meta <= 5) {
			crops.push(new CropRubber(getWorld(), block, meta, position));
		}

		ArrayList<Vect> candidates = processHarvestBlock(crops, seen, position);
		ArrayList<Vect> temp = new ArrayList<>();
		while (!candidates.isEmpty() && crops.size() < 100) {
			for (Vect candidate : candidates) {
				temp.addAll(processHarvestBlock(crops, seen, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}

		return crops;
	}

	private ArrayList<Vect> processHarvestBlock(Stack<ICrop> crops, Set<Vect> seen, Vect position) {
		World world = getWorld();

		ArrayList<Vect> candidates = new ArrayList<>();

		// Get additional candidates to return
		for (int j = 0; j < 2; j++) {
			Vect candidate = new Vect(position.x, position.y + j, position.z);
			if (candidate.equals(position)) {
				continue;
			}

			// See whether the given position has already been processed
			if (seen.contains(candidate)) {
				continue;
			}

			Block block = VectUtil.getBlock(world, candidate);
			if (ItemStackUtil.equals(block, PluginIC2.rubberwood)) {
				int meta = VectUtil.getBlockMeta(world, candidate);
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
