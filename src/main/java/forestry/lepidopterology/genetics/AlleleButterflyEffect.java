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

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IEffectData;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.core.genetics.alleles.AlleleCategorized;

public class AlleleButterflyEffect extends AlleleCategorized implements IAlleleButterflyEffect {

	public static IAlleleButterflyEffect butterflyNone;

	public static void createAlleles() {
		butterflyNone = new AlleleButterflyEffectNone();
		AlleleManager.alleleRegistry.registerAllele(butterflyNone, EnumButterflyChromosome.EFFECT);
	}

	protected AlleleButterflyEffect(String valueName, boolean isDominant) {
		super("forestry", "bf", valueName, isDominant);
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
	public IEffectData doEffect(IEntityButterfly butterfly, IEffectData storedData) {
		return storedData;
	}

}
