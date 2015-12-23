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

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;

public class BeeHousingModifier implements IBeeModifier {
	private final IBeeHousing beeHousing;

	public BeeHousingModifier(IBeeHousing beeHousing) {
		this.beeHousing = beeHousing;
	}

	@Override
	public float getTerritoryModifier(IBeeGenome genome, final float currentModifier) {
		float modifierValue = 1.0f;
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			modifierValue *= modifier.getTerritoryModifier(genome, modifierValue * currentModifier);
		}
		return modifierValue;
	}

	@Override
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, final float currentModifier) {
		float modifierValue = 1.0f;
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			modifierValue *= modifier.getMutationModifier(genome, mate, modifierValue * currentModifier);
		}
		return modifierValue;
	}

	@Override
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, final float currentModifier) {
		float modifierValue = 1.0f;
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			modifierValue *= modifier.getLifespanModifier(genome, mate, modifierValue * currentModifier);
		}
		return modifierValue;
	}

	@Override
	public float getProductionModifier(IBeeGenome genome, final float currentModifier) {
		float modifierValue = 1.0f;
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			modifierValue *= modifier.getProductionModifier(genome, modifierValue * currentModifier);
		}
		return modifierValue;
	}

	@Override
	public float getFloweringModifier(IBeeGenome genome, final float currentModifier) {
		float modifierValue = 1.0f;
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			modifierValue *= modifier.getFloweringModifier(genome, modifierValue * currentModifier);
		}
		return modifierValue;
	}

	@Override
	public float getGeneticDecay(IBeeGenome genome, final float currentModifier) {
		float modifierValue = 1.0f;
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			modifierValue *= modifier.getGeneticDecay(genome, modifierValue * currentModifier);
		}
		return modifierValue;
	}

	@Override
	public boolean isSealed() {
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			if (modifier.isSealed()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSelfLighted() {
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			if (modifier.isSelfLighted()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSunlightSimulated() {
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			if (modifier.isSunlightSimulated()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isHellish() {
		for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
			if (modifier.isHellish()) {
				return true;
			}
		}
		return false;
	}
}
