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

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.core.PluginCore;
import forestry.core.blocks.BlockBogEarth;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FarmLogicPeat extends FarmLogicWatered {
	private static final ItemStack bogEarth = PluginCore.getBlocks().bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, 1);

	public FarmLogicPeat() {
		super(bogEarth, PluginCore.getBlocks().bogEarth.getDefaultState());
	}

	@Override
	public boolean isAcceptedGround(IBlockState blockState) {
		if (super.isAcceptedGround(blockState)) {
			return true;
		}

		return blockState.getBlock() == PluginCore.getBlocks().bogEarth;
	}

	@Override
	public int getFertilizerConsumption() {
		return 2;
	}

	@Override
	public String getName() {
		if (isManual) {
			return "Manual Peat Bog";
		} else {
			return "Managed Peat Bog";
		}
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
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
		Stack<ICrop> crops = new Stack<>();
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			IBlockState blockState = world.getBlockState(position);
			Block block = blockState.getBlock();
			if (!(block instanceof BlockBogEarth)) {
				continue;
			}

			BlockBogEarth.SoilType soilType = BlockBogEarth.getTypeFromState(blockState);

			if (soilType == BlockBogEarth.SoilType.PEAT) {
				crops.push(new CropPeat(world, position));
			}
		}
		return crops;
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(PluginCore.getItems().peat);
	}
}
