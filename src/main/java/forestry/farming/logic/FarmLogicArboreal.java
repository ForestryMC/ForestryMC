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
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.BlockSoil;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.Vect;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class FarmLogicArboreal extends FarmLogicHomogeneous {

	public FarmLogicArboreal(IFarmHousing housing, ItemStack[] resource, ItemStack ground, IFarmable[] germlings) {
		super(housing, resource, ground, germlings);
	}

	public FarmLogicArboreal(IFarmHousing housing) {
		super(housing,
				new ItemStack[]{new ItemStack(Blocks.dirt)},
				ForestryBlock.soil.getItemStack(1, 0),
				Farmables.farmables.get("farmArboreal").toArray(new IFarmable[0]));
	}

	@Override
	public boolean isAcceptedGround(ItemStack itemStack) {
		if (super.isAcceptedGround(itemStack))
			return true;

		Block block = BlockSoil.getBlockFromItem(itemStack.getItem());
		if (block == null || !(block instanceof BlockSoil))
			return false;
		BlockSoil blockSoil = (BlockSoil)block;
		return blockSoil.getTypeFromMeta(itemStack.getItemDamage()) == BlockSoil.SoilType.HUMUS;
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

	@SuppressWarnings("unchecked")
	@Override
	public Collection<ItemStack> collect() {

		Collection<ItemStack> products = produce;
		produce = new ArrayList<ItemStack>();

		Vect coords = new Vect(housing.getCoords());
		Vect area = new Vect(housing.getArea());
		Vect offset = new Vect(housing.getOffset());

		Vect min = coords.add(offset);
		Vect max = coords.add(offset).add(area);

		AxisAlignedBB harvestBox = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, getWorld().getHeight(), max.z);
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
	public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {

		Collection<ICrop> crops = null;

		Vect start = new Vect(x, y, z);
		if (!lastExtentsHarvest.containsKey(start))
			lastExtentsHarvest.put(start, 0);

		int lastExtent = lastExtentsHarvest.get(start);
		if (lastExtent > extent)
			lastExtent = 0;

		Vect position = translateWithOffset(x, y + 1, z, direction, lastExtent);
		crops = getHarvestBlocks(position);
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
			ICrop crop = germl.getCropAt(world, position.x, position.y, position.z);
			if (crop == null)
				continue;

			crops.push(crop);
			seen.add(position);
			germling = germl;
			break;
		}

		if (germling == null)
			return crops;

		ArrayList<Vect> candidates = processHarvestBlock(germling, crops, seen, position, position);
		ArrayList<Vect> temp = new ArrayList<Vect>();
		while (!candidates.isEmpty()) {
			for (Vect candidate : candidates)
				temp.addAll(processHarvestBlock(germling, crops, seen, position, candidate));
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}

		return crops;
	}

	protected int yOffset = 0;

	private ArrayList<Vect> processHarvestBlock(IFarmable germling, Stack<ICrop> crops, Set<Vect> seen, Vect start, Vect position) {

		World world = getWorld();

		ArrayList<Vect> candidates = new ArrayList<Vect>();

		// Get additional candidates to return
		for (int i = -1; i < 2; i++)
			for (int j = yOffset; j < 2; j++)
				for (int k = -1; k < 2; k++) {
					Vect candidate = new Vect(position.x + i, position.y + j, position.z + k);
					if (candidate.equals(position))
						continue;
					if (Math.abs(candidate.x - start.x) > 5)
						continue;
					if (Math.abs(candidate.z - start.z) > 5)
						continue;

					// See whether the given position has already been processed
					if (seen.contains(candidate))
						continue;

					ICrop crop = germling.getCropAt(world, candidate.x, candidate.y, candidate.z);
					if (crop != null) {
						crops.push(crop);
						candidates.add(candidate);
						seen.add(candidate);
					}
				}

		return candidates;
	}

	@Override
	protected boolean maintainGermlings(int x, int ySaplings, int z, ForgeDirection direction, int extent) {

		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, ySaplings, z, direction, i);

			if (isAirBlock(position)) {
				Vect soilBelowPosition = new Vect(position.x, position.y - 1, position.z);
				ItemStack soilBelow = getAsItemStack(soilBelowPosition);
				if (isAcceptedGround(soilBelow))
					return plantSapling(position);
			}
		}
		return false;
	}

	private boolean plantSapling(Vect position) {

		World world = getWorld();
		for (IFarmable candidate : germlings)
			if (housing.plantGermling(candidate, world, position.x, position.y, position.z))
				return true;

		return false;
	}

}
