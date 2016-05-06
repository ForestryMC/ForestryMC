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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;

public class FarmLogicInfernal extends FarmLogicHomogeneous {

	public FarmLogicInfernal(IFarmHousing housing) {
		super(housing, new ItemStack(Blocks.SOUL_SAND), new ItemStack(Blocks.SOUL_SAND), Farmables.farmables.get("farmInfernal"));
	}

	@Override
	public String getName() {
		return "Managed Infernal Farm";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem() {
		return Items.NETHER_WART;
	}

	@Override
	public int getFertilizerConsumption() {
		return 20;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return 0;
	}

	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public Collection<ICrop> harvest(BlockPos pos, FarmDirection direction, int extent) {
		World world = getWorld();

		Stack<ICrop> crops = new Stack<>();
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos.add(0, 1, 0), direction, i);
			for (IFarmable farmable : germlings) {
				ICrop crop = farmable.getCropAt(world, position);
				if (crop != null) {
					crops.push(crop);
				}
			}

		}
		return crops;

	}

	@Override
	protected boolean maintainGermlings(BlockPos pos, FarmDirection direction, int extent) {
		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			IBlockState blockState = world.getBlockState(position);
			if (!world.isAirBlock(position) && !BlockUtil.isReplaceableBlock(blockState, world, position)) {
				continue;
			}

			BlockPos soilPosition = position.down();
			IBlockState soilState = world.getBlockState(soilPosition);
			Block soilBlock = soilState.getBlock();
			ItemStack soilStack = soilBlock.getPickBlock(soilState, null, world, soilPosition, null);
			if (!isAcceptedSoil(soilStack)) {
				continue;
			}

			return trySetCrop(position);
		}

		return false;
	}

	private boolean trySetCrop(BlockPos position) {
		World world = getWorld();

		for (IFarmable candidate : germlings) {
			if (housing.plantGermling(candidate, world, position)) {
				return true;
			}
		}

		return false;
	}

}
