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
import java.util.List;
import java.util.Stack;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.config.Defaults;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.core.vect.IVect;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;

public abstract class FarmLogicCrops extends FarmLogicWatered {

	private final Iterable<IFarmable> seeds;
	private static final ItemStack farmland = new ItemStack(Blocks.farmland, 1, Defaults.WILDCARD);

	public FarmLogicCrops(IFarmHousing housing, Iterable<IFarmable> seeds) {
		super(housing, new ItemStack(Blocks.dirt), new ItemStack(Blocks.farmland));

		this.seeds = seeds;
	}

	@Override
	public boolean isAcceptedGround(ItemStack itemStack) {
		return super.isAcceptedGround(itemStack) || StackUtils.isIdenticalItem(farmland, itemStack);
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		for (IFarmable germling : seeds) {
			if (germling.isGermling(itemstack)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWindfall(ItemStack itemstack) {
		for (IFarmable germling : seeds) {
			if (germling.isWindfall(itemstack)) {
				return true;
			}
		}
		return false;
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

		AxisAlignedBB harvestBox = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
		List<Entity> list = housing.getWorld().getEntitiesWithinAABB(Entity.class, harvestBox);

		int i;
		for (i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);

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

	@Override
	protected boolean maintainCrops(int x, int y, int z, FarmDirection direction, int extent) {

		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			if (!VectUtil.isAirBlock(world, position) && !Utils.isReplaceableBlock(getWorld(), position.x, position.y, position.z)) {
				continue;
			}

			ItemStack below = VectUtil.getAsItemStack(world, position.add(0, -1, 0));
			if (ground.getItem() != below.getItem()) {
				continue;
			}
			if (below.getItemDamage() <= 0) {
				continue;
			}

			return trySetCrop(position);
		}

		return false;
	}

	private boolean trySetCrop(IVect position) {
		World world = getWorld();

		for (IFarmable candidate : seeds) {
			if (housing.plantGermling(candidate, world, position.getX(), position.getY(), position.getZ())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, FarmDirection direction, int extent) {
		World world = getWorld();

		Stack<ICrop> crops = new Stack<ICrop>();
		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y + 1, z, direction, i);
			for (IFarmable seed : seeds) {
				ICrop crop = seed.getCropAt(world, position.x, position.y, position.z);
				if (crop != null) {
					crops.push(crop);
				}
			}
		}
		return crops;
	}

}
