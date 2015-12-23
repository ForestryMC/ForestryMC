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

import java.awt.Color;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflySpeciesCustom;
import forestry.api.lepidopterology.IButterflyFactory;

public class ButterflyFactory implements IButterflyFactory {
	@Override
	public IAlleleButterflySpeciesCustom createSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, String texturePath, boolean isDominant, IClassification branch, String binomial, Color serumColour) {
		IAlleleButterflySpeciesCustom species = new AlleleButterflySpecies(uid, unlocalizedName, authority, unlocalizedDescription, texturePath, isDominant, branch, binomial, serumColour);
		AlleleManager.alleleRegistry.registerAllele(species, EnumButterflyChromosome.SPECIES);
		return species;
	}
}
