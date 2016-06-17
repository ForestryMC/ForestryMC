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

import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterflyMutationBuilder;
import forestry.api.lepidopterology.IButterflyMutationFactory;

public class ButterflyMutationFactory implements IButterflyMutationFactory {

	@Override
	public IButterflyMutationBuilder createMutation(IAlleleButterflySpecies parentButterfly0, IAlleleButterflySpecies parentButterfly1, IAllele[] result, int chance) {
		ButterflyMutation butterflyMutation = new ButterflyMutation(parentButterfly0, parentButterfly1, result, chance);
		ButterflyManager.butterflyRoot.registerMutation(butterflyMutation);
		return butterflyMutation;
	}

}
