/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

/**
 * Easily extendable default IBeeModifier.
 * By itself, this IBeeModifier does nothing.
 * BeeModifiers should inherit from this class unless they modify everything.
 */
public class DefaultBeeModifier implements IBeeModifier {

	public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
		return 1.0f;
	}

	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		return 1.0f;
	}

	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
		return 1.0f;
	}

	public float getProductionModifier(IBeeGenome genome, float currentModifier) {
		return 1.0f;
	}

	public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
		return 1.0f;
	}

	public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
		return 1.0f;
	}

	public boolean isSealed() {
		return false;
	}

	public boolean isSelfLighted() {
		return false;
	}

	public boolean isSunlightSimulated() {
		return false;
	}

	public boolean isHellish() {
		return false;
	}

}
