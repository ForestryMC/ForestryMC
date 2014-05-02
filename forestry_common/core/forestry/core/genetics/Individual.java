/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.IIndividual;

public abstract class Individual implements IIndividual {

	protected boolean isAnalyzed = false;

	@Override
	public boolean isAnalyzed() {
		return isAnalyzed;
	}

	@Override
	public boolean analyze() {
		if (isAnalyzed)
			return false;

		isAnalyzed = true;
		return true;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (nbttagcompound == null)
			return;

		isAnalyzed = nbttagcompound.getBoolean("IsAnalyzed");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setBoolean("IsAnalyzed", isAnalyzed);
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
	public boolean isPureBred(int chromosomeOrdinal) {
		return getGenome().getActiveAllele(chromosomeOrdinal).getUID().equals(getGenome().getInactiveAllele(chromosomeOrdinal).getUID());
	}

}
