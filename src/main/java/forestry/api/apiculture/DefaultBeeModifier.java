/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import javax.annotation.Nullable;

import genetics.api.individual.IGenome;

/**
 * Easily extendable default IBeeModifier.
 * By itself, this IBeeModifier does nothing.
 * BeeModifiers should inherit from this class unless they modify everything.
 */
public class DefaultBeeModifier implements IBeeModifier {

	@Override
	public float getTerritoryModifier(IGenome genome, float currentModifier) {
		return 1.0f;
	}

	@Override
	public float getMutationModifier(IGenome genome, IGenome mate, float currentModifier) {
		return 1.0f;
	}

	@Override
	public float getLifespanModifier(IGenome genome, @Nullable IGenome mate, float currentModifier) {
		return 1.0f;
	}

	@Override
	public float getProductionModifier(IGenome genome, float currentModifier) {
		return 1.0f;
	}

	@Override
	public float getFloweringModifier(IGenome genome, float currentModifier) {
		return 1.0f;
	}

	@Override
	public float getGeneticDecay(IGenome genome, float currentModifier) {
		return 1.0f;
	}

	@Override
	public boolean isSealed() {
		return false;
	}

	@Override
	public boolean isSelfLighted() {
		return false;
	}

	@Override
	public boolean isSunlightSimulated() {
		return false;
	}

	@Override
	public boolean isHellish() {
		return false;
	}

}
