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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class FarmLogicOrchardBasic extends FarmLogic {

	private final IFarmable[] germlings;

	public FarmLogicOrchardBasic(IFarmHousing housing) {
		super(housing);
		Collection<IFarmable> farmables = Farmables.farmables.get("farmOrchardBasic");
		germlings = farmables.toArray(new IFarmable[farmables.size()]);	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return Items.dye.getIconFromDamage(2);
	}

	@Override
	public String getName() {
		return "Basic Orchard";
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return 1;
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {	return false;	}

	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {	return false;	}

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
		Block bearer = getFruitBlock(position);
		Block block = VectUtil.getBlock(world, position);
		if ((!block.isWood(getWorld(), position.x, position.y, position.z)) && bearer == null) {
			return crops;
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

		return crops;
	}

	private ArrayList<Vect> processHarvestBlock(Stack<ICrop> crops, Set<Vect> seen, Vect start, Vect position) {
		World world = getWorld();

		ArrayList<Vect> candidates = new ArrayList<Vect>();

		// Get additional candidates to return
		for (int i = -1; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					Vect candidate = new Vect(position.x + i, position.y + j, position.z + k);
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
					Block bearer = getFruitBlock(candidate);
					if (bearer != null) {
						candidates.add(candidate);
						seen.add(candidate);
						for (IFarmable seed : germlings) {
							ICrop crop = seed.getCropAt(world, candidate.x, candidate.y, candidate.z);
							if (crop != null) {
								crops.push(crop);
							}
						}

					} else if (VectUtil.isWoodBlock(world, candidate)) {
						candidates.add(candidate);
						seen.add(candidate);
					}
				}
			}
		}

		return candidates;
	}

	private Block getFruitBlock(Vect position) {
		World world = getWorld();
		for (IFarmable seed : germlings) {
		if (seed.isSaplingAt(world, position.x, position.y, position.z)){
			return VectUtil.getBlock(world, position);
		}
		}
		return null;
	}

}
