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
package forestry.core.genetics;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.StackUtils;

public class MutationConditionRequiresResource implements IMutationCondition {

	private final ItemStack blockRequired;

	public MutationConditionRequiresResource(Block block, int meta) {
		blockRequired = new ItemStack(block, meta);
	}

	@Override
	public float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		BlockPos posNEW = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
		IBlockState state = world.getBlockState(posNEW);
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		return StackUtils.equals(block, meta, blockRequired) ? 1 : 0;
	}

	@Override
	public String getDescription() {
		return String.format("Must be placed on:%s", blockRequired.getDisplayName());
	}
}
