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
package forestry.apiculture.genetics;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.apiculture.IBeeMutationFactory;
import forestry.api.genetics.IAllele;

public class BeeMutationFactory implements IBeeMutationFactory {

	@Override
	public IBeeMutationBuilder createMutation(IAlleleBeeSpecies parentBee0, IAlleleBeeSpecies parentBee1, IAllele[] result, int chance) {
		BeeMutation beeMutation = new BeeMutation(parentBee0, parentBee1, result, chance);
		BeeManager.beeRoot.registerMutation(beeMutation);
		return beeMutation;
	}
}
