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
package forestry.core.genetics.mutations;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.Translator;

public class MutationConditionRequiresResource implements IMutationCondition {

	private final IBlockState requiredBlockState;

	public MutationConditionRequiresResource(IBlockState requiredBlockState) {
		this.requiredBlockState = requiredBlockState;
	}

	@Override
	public float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		IBlockState blockState;
		do {
			pos = pos.down();
			blockState = world.getBlockState(pos);
		} while (blockState.getBlock() instanceof IBeeHousing);

		return this.requiredBlockState == blockState ? 1 : 0;
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocalFormatted("for.mutation.condition.resource", requiredBlockState.getBlock().getLocalizedName());
	}
}
