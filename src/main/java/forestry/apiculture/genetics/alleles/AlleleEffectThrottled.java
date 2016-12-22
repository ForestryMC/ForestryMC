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
package forestry.apiculture.genetics.alleles;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.EffectData;

public abstract class AlleleEffectThrottled extends AlleleEffect {

	private boolean isCombinable = false;
	private final int throttle;
	private boolean requiresWorkingQueen = false;

	protected AlleleEffectThrottled(String name, boolean isDominant, int throttle, boolean requiresWorking, boolean isCombinable) {
		super(name, isDominant);
		this.throttle = throttle;
		this.isCombinable = isCombinable;
		this.requiresWorkingQueen = requiresWorking;
	}

	@Override
	public boolean isCombinable() {
		return isCombinable;
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		if (storedData instanceof EffectData) {
			return storedData;
		}

		return new EffectData(1, 0);
	}

	@Override
	public final IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (isThrottled(storedData, housing)) {
			return storedData;
		}
		return doEffectThrottled(genome, storedData, housing);
	}

	private boolean isThrottled(IEffectData storedData, IBeeHousing housing) {

		if (requiresWorkingQueen && housing.getErrorLogic().hasErrors()) {
			return true;
		}

		int time = storedData.getInteger(0);
		time++;
		storedData.setInteger(0, time);

		if (time < throttle) {
			return true;
		}

		// Reset since we are done throttling.
		storedData.setInteger(0, 0);
		return false;
	}

	abstract IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing);

}
