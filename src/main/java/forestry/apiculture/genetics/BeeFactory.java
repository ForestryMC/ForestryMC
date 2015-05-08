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

import forestry.api.apiculture.IAlleleBeeSpeciesCustom;
import forestry.api.apiculture.IBeeFactory;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;

public class BeeFactory implements IBeeFactory {
	@Override
	public IAlleleBeeSpeciesCustom createSpecies(String uid, boolean dominant, String authority, String unlocalizedName, String unlocalizedDescription, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		IAlleleBeeSpeciesCustom species = new AlleleBeeSpecies(uid, authority, unlocalizedDescription, dominant, unlocalizedName, branch, binomial, primaryColor, secondaryColor);
		AlleleManager.alleleRegistry.registerAllele(species);
		return species;
	}

	@Override
	public IClassification createBranch(String uid, String scientific) {
		return new BranchBees(uid, scientific);
	}
}
