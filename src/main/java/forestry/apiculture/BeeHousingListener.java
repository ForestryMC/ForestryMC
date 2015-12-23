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
package forestry.apiculture;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeListener;
import forestry.api.genetics.IIndividual;

public class BeeHousingListener implements IBeeListener {
	private final IBeeHousing beeHousing;

	public BeeHousingListener(IBeeHousing beeHousing) {
		this.beeHousing = beeHousing;
	}

	@Override
	public void wearOutEquipment(int amount) {
		for (IBeeListener beeListener : beeHousing.getBeeListeners()) {
			beeListener.wearOutEquipment(amount);
		}
	}

	@Override
	public void onQueenDeath() {
		for (IBeeListener beeListener : beeHousing.getBeeListeners()) {
			beeListener.onQueenDeath();
		}
	}

	@Override
	public boolean onPollenRetrieved(IIndividual pollen) {
		for (IBeeListener beeListener : beeHousing.getBeeListeners()) {
			if (beeListener.onPollenRetrieved(pollen)) {
				return true;
			}
		}

		return false;
	}

}
