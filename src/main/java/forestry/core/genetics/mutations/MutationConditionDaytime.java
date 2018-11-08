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

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.utils.Translator;

public class MutationConditionDaytime implements IMutationCondition {

	private final boolean daytime;

	public MutationConditionDaytime(boolean daytime) {
		this.daytime = daytime;
	}

	@Override
	public float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
		if (world.isDaytime() == daytime) {
			return 1;
		}
		return 0;
	}

	@Override
	public String getDescription() {
		if (daytime) {
			return Translator.translateToLocal("for.mutation.condition.daytime.day");
		} else {
			return Translator.translateToLocal("for.mutation.condition.daytime.night");
		}
	}
}
