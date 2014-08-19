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
package forestry.lepidopterology.genetics;

import forestry.api.genetics.IEffectData;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.core.genetics.Allele;
import forestry.core.utils.StringUtil;

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
		return StringUtil.localize("lepidopterology.effect.none");
	}

	@Override
	public IEffectData doEffect(IEntityButterfly butterfly, IEffectData storedData) {
		return storedData;
	}

}
