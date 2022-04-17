/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.IIndividual;

/**
 * Easily extendable default IBeeListener.
 * By itself, this IBeeListener does nothing.
 * BeeListeners should inherit from this class unless they need to listen for everything.
 */
public class DefaultBeeListener implements IBeeListener {
	@Override
	public void wearOutEquipment(int amount) {

	}

	@Override
	public void onQueenDeath() {

	}

	@Override
	public boolean onPollenRetrieved(IIndividual pollen) {
		return false;
	}

}
