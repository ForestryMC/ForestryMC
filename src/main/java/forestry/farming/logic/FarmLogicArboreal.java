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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.BlockSoil;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;

public class FarmLogicArboreal extends FarmLogicHomogeneous {

	public FarmLogicArboreal(IFarmHousing housing, ItemStack resource, ItemStack ground,
			Iterable<IFarmable> germlings) {
		super(housing, resource, ground, germlings);
	}

	public FarmLogicArboreal(IFarmHousing housing) {
		super(housing, new ItemStack(Blocks.dirt), ForestryBlock.soil.getItemStack(1, 0),
				Farmables.farmables.get("farmArboreal"));
	}

	@Override
	public boolean isAcceptedSoil(ItemStack soil) {
		if (super.isAcceptedSoil(soil)) {
			return true;
		}

		Block block = Block.getBlockFromItem(soil.getItem());
		if (block == null || !(block instanceof BlockSoil)) {
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

	@SuppressWarnings("unchecked")
	@Override
	public Collection<ItemStack> collect() {

		Collection<ItemStack> products = produce;
		produce = new ArrayList<ItemStack>();

		Vect coords = new Vect(housing.getCoords());
		Vect area = new Vect(housing.getArea());
		Vect offset = new Vect(housing.getOffset());

		Vect min = coords.add(offset);
		Vect max = min.add(area);

		AxisAlignedBB harvestBox = AxisAlignedBB.fromBounds(min.getX(), min.getY(), min.getZ(), max.getX(),
				getWorld().getHeight(), max.getZ());
		List<Entity> list = getWorld().getEntitiesWithinAABB(Entity.class, harvestBox);

		for (Entity entity : list) {
			if (entity instanceof EntityItem) {
				EntityItem item = (EntityItem) entity;
				if (!item.isDead) {
					ItemStack contained = item.getEntityItem();
					if (isAcceptedGermling(contained) || isWindfall(contained)) {
						produce.add(contained.copy());
						item.setDead();
					}
				}
			}
		}

		return products;
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

	private Collection<ICrop> getHarvestBlocks(Vect position) {

		World world = getWorld();

		Set<Vect> seen = new HashSet<Vect>();
		Stack<ICrop> crops = new Stack<ICrop>();

		// Determine what type we want to harvest.
		IFarmable germling = null;
		for (IFarmable germl : germlings) {
			ICrop crop = germl.getCropAt(world, position.pos);
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
		ArrayList<Vect> temp = new ArrayList<Vect>();
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

	protected int yOffset = 0;

	private ArrayList<Vect> processHarvestBlock(IFarmable germling, Stack<ICrop> crops, Set<Vect> seen, Vect start,
			Vect position) {

		World world = getWorld();

		ArrayList<Vect> candidates = new ArrayList<Vect>();

		// Get additional candidates to return
		for (int i = -1; i < 2; i++) {
			for (int j = yOffset; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					Vect candidate = position.add(i, j, k);
					if (candidate.equals(position)) {
						continue;
					}
					if (Math.abs(candidate.pos.getX() - start.pos.getX()) > 5) {
						continue;
					}
					if (Math.abs(candidate.pos.getZ() - start.pos.getZ()) > 5) {
						continue;
					}

					// See whether the given position has already been processed
					if (seen.contains(candidate)) {
						continue;
					}

					ICrop crop = germling.getCropAt(world, candidate.pos);
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
	protected boolean maintainGermlings(BlockPos pos, FarmDirection direction, int extent) {

		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(pos, direction, i);

			if (VectUtil.isAirBlock(world, position)) {
				Vect soilBelowPosition = new Vect(position.getX(), position.getY() - 1, position.getZ());
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
		for (IFarmable candidate : germlings) {
			if (housing.plantGermling(candidate, world, position.pos)) {
				return true;
			}
		}

		return false;
	}

	public static void registerSprites() {

	}

}
