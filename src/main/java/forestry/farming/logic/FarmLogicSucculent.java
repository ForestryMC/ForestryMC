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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.ItemStackUtil;

public class FarmLogicSucculent extends FarmLogic {
	private final Collection<IFarmable> germlings = Farmables.farmables.get("farmSucculentes");

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Items.DYE, 1, 2);
	}

	@Override
	public String getName() {
		if (isManual) {
			return "Manual Succulent Farm";
		} else {
			return "Managed Succulent Farm";
		}
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
	public boolean isAcceptedResource(ItemStack itemstack) {
		if (isManual) {
			return false;
		}

		return ItemStackUtil.equals(Blocks.SAND, itemstack);
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		if (isManual) {
			return false;
		}

		return ItemStackUtil.equals(Blocks.CACTUS, itemstack);
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack stack) {
		return false;
	}

	@Override
	public Collection<ItemStack> collect(World world, IFarmHousing farmHousing) {
		return null;
	}

	@Override
	public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		return false;
	}

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
		Stack<ICrop> crops = new Stack<>();
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos.up(), direction, i);
			IBlockState blockState = world.getBlockState(position);
			for (IFarmable seed : germlings) {
				ICrop crop = seed.getCropAt(world, position, blockState);
				if (crop != null) {
					crops.push(crop);
					break;
				}
			}
		}
		return crops;
	}

}
