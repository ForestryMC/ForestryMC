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
package forestry.arboriculture.genetics.alleles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import genetics.api.alleles.AlleleCategorized;
import genetics.api.individual.IGenome;

import forestry.api.arboriculture.genetics.IAlleleLeafEffect;
import forestry.api.genetics.IEffectData;
import forestry.core.config.Constants;

public class AlleleLeafEffect extends AlleleCategorized implements IAlleleLeafEffect {

	protected AlleleLeafEffect(String valueName, boolean isDominant) {
		super(Constants.MOD_ID, "leaves", valueName, isDominant);
	}

	@Override
	public boolean isCombinable() {
		return true;
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		return storedData;
	}

	@Override
	public IEffectData doEffect(IGenome genome, IEffectData storedData, Level world, BlockPos pos) {
		return storedData;
	}
}
