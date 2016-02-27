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

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.StringUtil;

public class MutationConditionDaytime implements IMutationCondition {

	private final boolean daytime;

	public MutationConditionDaytime(boolean daytime) {
		this.daytime = daytime;
	}

	@Override
	public <C extends IChromosomeType> float getChance(World world, BlockPos pos, IAlleleSpecies<C> species0, IAlleleSpecies<C> species1, IGenome<C> genome0, IGenome<C> genome1) {
		if (world.isDaytime() == daytime) {
			return 1;
		}
		return 0;
	}

	@Override
	public String getDescription() {
		if (daytime) {
			return StringUtil.localize("mutation.condition.daytime.day");
		} else {
			return StringUtil.localize("mutation.condition.daytime.night");
		}
	}
}
