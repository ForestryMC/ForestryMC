/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.lepidopterology.genetics;

import forestry.api.genetics.IEffectData;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.core.genetics.Allele;

public class AlleleEffectNone extends Allele implements IAlleleButterflyEffect {

	public AlleleEffectNone(String uid, boolean isDominant) {
		super(uid, isDominant, false);
	}

	@Override
	public boolean isCombinable() {
		return true;
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		return null;
	}

	@Override
	public String getName() {
		return "None";
	}

	@Override
	public IEffectData doEffect(IEntityButterfly butterfly, IEffectData storedData) {
		return storedData;
	}

}
