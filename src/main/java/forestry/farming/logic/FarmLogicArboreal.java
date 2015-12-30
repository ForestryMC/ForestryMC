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
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.blocks.BlockSoil;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.vect.Vect;
import forestry.core.utils.vect.VectUtil;
import forestry.plugins.PluginCore;

public class FarmLogicArboreal extends FarmLogicHomogeneous {
	private static final int BRANCH_RANGE = 20;

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

		Block block = BlockSoil.getBlockFromItem(soil.getItem());
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
	public IIcon getIcon() {
		return Blocks.sapling.getBlockTextureFromSide(0);
	}

	@Override
	public ResourceLocation getSpriteSheet() {
		return SpriteSheet.BLOCKS.getLocation();
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

	private final HashMap<Vect, Integer> lastExtentsHarvest = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, FarmDirection direction, int extent) {

		Vect start = new Vect(x, y, z);
		if (!lastExtentsHarvest.containsKey(start)) {
			lastExtentsHarvest.put(start, 0);
		}

		int lastExtent = lastExtentsHarvest.get(start);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		Vect position = translateWithOffset(x, y + 1, z, direction, lastExtent);
		Collection<ICrop> crops = getHarvestBlocks(position);
		lastExtent++;
		lastExtentsHarvest.put(start, lastExtent);

		return crops;
	}

	private Collection<ICrop> getHarvestBlocks(Vect position) {

		World world = getWorld();

		Set<Vect> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		// Determine what type we want to harvest.
		IFarmable germling = null;
		for (IFarmable germl : germlings) {
			ICrop crop = germl.getCropAt(world, position.x, position.y, position.z);
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

		ArrayList<Vect> candidates = processHarvestBlock(germling, crops, seen, position, position);
		ArrayList<Vect> temp = new ArrayList<>();
		while (!candidates.isEmpty()) {
			for (Vect candidate : candidates) {
				temp.addAll(processHarvestBlock(germling, crops, seen, position, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}

		return crops;
	}

	private ArrayList<Vect> processHarvestBlock(IFarmable germling, Stack<ICrop> crops, Set<Vect> seen, Vect start, Vect position) {

		World world = getWorld();

		ArrayList<Vect> candidates = new ArrayList<>();

		// Get additional candidates to return
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				for (int z = -1; z < 2; z++) {
					Vect candidate = position.add(x, y, z);
					if (candidate.equals(position)) {
						continue;
					}
					if (Math.abs(candidate.x - start.x) > BRANCH_RANGE) {
						continue;
					}
					if (Math.abs(candidate.z - start.z) > BRANCH_RANGE) {
						continue;
					}

					// See whether the given position has already been processed
					if (seen.contains(candidate)) {
						continue;
					}

					ICrop crop = germling.getCropAt(world, candidate.x, candidate.y, candidate.z);
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
			Vect position = translateWithOffset(x, ySaplings, z, direction, i);

			if (VectUtil.isAirBlock(world, position)) {
				Vect soilBelowPosition = new Vect(position.x, position.y - 1, position.z);
				ItemStack soilBelow = VectUtil.getAsItemStack(world, soilBelowPosition);
				if (isAcceptedSoil(soilBelow)) {
					return plantSapling(position);
				}
			}
		}
		return false;
	}

	private boolean plantSapling(Vect position) {
		World world = getWorld();
		Collections.shuffle(germlings);
		for (IFarmable candidate : germlings) {
			if (housing.plantGermling(candidate, world, position.x, position.y, position.z)) {
				return true;
			}
		}

		return false;
	}

}
