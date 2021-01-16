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

import javax.annotation.Nullable;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.DefaultBeeModifier;

public class BeehouseBeeModifier extends DefaultBeeModifier {
	@Override
	public float getProductionModifier(IGenome genome, float currentModifier) {
		return 0.25f;
	}

	@Override
	public float getMutationModifier(IGenome genome, IGenome mate, float currentModifier) {
		return 0.0f;
	}

	@Override
	public float getLifespanModifier(IGenome genome, @Nullable IGenome mate, float currentModifier) {
		return 3.0f;
	}

	@Override
	public float getFloweringModifier(IGenome genome, float currentModifier) {
		return 3.0f;
	}

	@Override
	public float getGeneticDecay(IGenome genome, float currentModifier) {
		return 0.0f;
	}
}
