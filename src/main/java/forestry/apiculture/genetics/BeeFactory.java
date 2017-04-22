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

import com.google.common.base.Preconditions;
import forestry.api.apiculture.IAlleleBeeSpeciesBuilder;
import forestry.api.apiculture.IBeeFactory;
import forestry.api.genetics.IClassification;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;

public class BeeFactory implements IBeeFactory {
	@Override
	public IAlleleBeeSpeciesBuilder createSpecies(
			String uid,
			boolean dominant,
			String authority,
			String unlocalizedName,
			String unlocalizedDescription,
			IClassification branch,
			String binomial,
			int primaryColor,
			int secondaryColor) {
		Preconditions.checkNotNull(uid);
		Preconditions.checkNotNull(authority);
		Preconditions.checkNotNull(unlocalizedName);
		Preconditions.checkNotNull(unlocalizedDescription);
		Preconditions.checkNotNull(branch);
		Preconditions.checkNotNull(binomial);

		return new AlleleBeeSpecies(uid, unlocalizedName, authority, unlocalizedDescription, dominant, branch, binomial, primaryColor, secondaryColor);
	}

	@Override
	public IClassification createBranch(String uid, String scientific) {
		return new BranchBees(uid, scientific);
	}
}
