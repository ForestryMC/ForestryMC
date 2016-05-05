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

import java.util.Collection;
import java.util.Stack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.vect.IVect;
import forestry.core.utils.vect.Vect;
import forestry.core.utils.vect.VectUtil;

public abstract class FarmLogicCrops extends FarmLogicWatered {
	private static final ItemStack farmland = new ItemStack(Blocks.FARMLAND, 1, OreDictionary.WILDCARD_VALUE);
	private final Iterable<IFarmable> seeds;

	protected FarmLogicCrops(IFarmHousing housing, Iterable<IFarmable> seeds) {
		super(housing, new ItemStack(Blocks.DIRT), new ItemStack(Blocks.FARMLAND));

		this.seeds = seeds;
	}

	@Override
	public boolean isAcceptedGround(ItemStack itemStack) {
		return super.isAcceptedGround(itemStack) || ItemStackUtil.isIdenticalItem(farmland, itemStack);
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

	@Override
	public boolean isAcceptedWindfall(ItemStack itemstack) {
		for (IFarmable germling : seeds) {
			if (germling.isWindfall(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<ItemStack> collect() {
		Collection<ItemStack> products = produce;
		produce = collectEntityItems(false);
		return products;
	}

	@Override
	protected boolean maintainCrops(BlockPos pos, FarmDirection direction, int extent) {

		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(pos, direction, i);
			IBlockState state = world.getBlockState(position);
			if (!VectUtil.isAirBlock(world, position) && !BlockUtil.isReplaceableBlock(state, world, position)) {
				continue;
			}

			ItemStack below = VectUtil.getAsItemStack(world, position.down());
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
			if (housing.plantGermling(candidate, world, position)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Collection<ICrop> harvest(BlockPos pos, FarmDirection direction, int extent) {
		World world = getWorld();

		Stack<ICrop> crops = new Stack<>();
		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(pos.add(0, 1, 0), direction, i);
			for (IFarmable seed : seeds) {
				ICrop crop = seed.getCropAt(world, position);
				if (crop != null) {
					crops.push(crop);
				}
			}
		}
		return crops;
	}

}
