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

import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import net.minecraft.nbt.NBTTagCompound;

public abstract class Individual implements IIndividual {
	protected boolean isAnalyzed;

	protected Individual() {
		isAnalyzed = false;
	}

	protected Individual(NBTTagCompound nbt) {
		isAnalyzed = nbt.getBoolean("IsAnalyzed");
	}

	@Override
	public boolean isAnalyzed() {
		return isAnalyzed;
	}

	@Override
	public boolean analyze() {
		if (isAnalyzed) {
			return false;
		}

		isAnalyzed = true;
		return true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setBoolean("IsAnalyzed", isAnalyzed);
		return nbttagcompound;
	}

	/* IDENTIFICATION */
	@Override
	public String getIdent() {
		return getGenome().getPrimary().getUID();
	}

	@Override
	public String getDisplayName() {
		return getGenome().getPrimary().getName();
	}

	/* INFORMATION */
	@Override
	public boolean hasEffect() {
		return getGenome().getPrimary().hasEffect();
	}

	@Override
	public boolean isSecret() {
		return getGenome().getPrimary().isSecret();
	}

	@Override
	public boolean isGeneticEqual(IIndividual other) {
		return getGenome().isGeneticEqual(other.getGenome());
	}

	@Override
	public boolean isPureBred(IChromosomeType chromosomeType) {
		return getGenome().getActiveAllele(chromosomeType).getUID().equals(getGenome().getInactiveAllele(chromosomeType).getUID());
	}
}
