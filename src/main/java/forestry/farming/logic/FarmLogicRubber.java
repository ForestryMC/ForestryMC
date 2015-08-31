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
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.sprite.ISprite;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;
import forestry.plugins.PluginIC2;

public class FarmLogicRubber extends FarmLogic {

	private boolean inActive;

	public FarmLogicRubber(IFarmHousing housing) {
		super(housing);
		if (PluginIC2.rubberwood == null || PluginIC2.resin == null) {
			Proxies.log.warning("Failed to init a farm logic %s since IC2 was not found", getClass().getName());
			inActive = true;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISprite getIcon() {
		if (!inActive) {
			return TextureManager.getInstance().getFarmSprite("farmLoigc", "ic2", "items", "itemRubber");
		} else {
			return TextureManager.getInstance().getFarmSprite("farmLoigc", "items", "gunpowder");
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
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean cultivate(BlockPos pos, FarmDirection direction, int extent) {
		return false;
	}

	private final HashMap<Vect, Integer> lastExtents = new HashMap<Vect, Integer>();

	@Override
	public Collection<ICrop> harvest(BlockPos pos, FarmDirection direction, int extent) {
		if (inActive) {
			return null;
		}

		Vect start = new Vect(pos);
		if (!lastExtents.containsKey(start)) {
			lastExtents.put(start, 0);
		}

		int lastExtent = lastExtents.get(start);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		Vect position = translateWithOffset(pos, direction, lastExtent);
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
		Block block = VectUtil.getBlock(world, position);
		if (!StackUtils.equals(block, PluginIC2.rubberwood)) {
			return crops;
		}

		int meta = VectUtil.getBlockMeta(world, position);
		if (meta >= 2 && meta <= 5) {
			crops.push(new CropRubber(getWorld(), block, meta, position));
		}

		ArrayList<Vect> candidates = processHarvestBlock(crops, seen, position);
		ArrayList<Vect> temp = new ArrayList<Vect>();
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

		ArrayList<Vect> candidates = new ArrayList<Vect>();

		// Get additional candidates to return
		for (int j = 0; j < 2; j++) {
			Vect candidate = new Vect(position.getX(), position.getY() + j, position.getZ());
			if (candidate.equals(position)) {
				continue;
			}

			// See whether the given position has already been processed
			if (seen.contains(candidate)) {
				continue;
			}

			Block block = VectUtil.getBlock(world, candidate);
			if (StackUtils.equals(block, PluginIC2.rubberwood)) {
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
