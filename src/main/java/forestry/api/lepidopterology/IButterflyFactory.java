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
package forestry.api.lepidopterology;

import java.awt.Color;

import forestry.api.genetics.IClassification;

public interface IButterflyFactory {
	/**
	 * Creates a new butterfly species.
	 * Automatically registered with AlleleManager.alleleRegistry.registerAllele()
	 * See IAlleleButterflySpeciesCustom and IAlleleSpeciesCustom for adding additional properties to the returned species.
	 *
	 * @param uid Unique Identifier for this species
	 * @param unlocalizedName Unlocalized name for this species
	 * @param authority Authority for the binomial name, e.g. "Sengir" on species of base Forestry.
	 * @param unlocalizedDescription Unlocalized description for this species
	 * @param texturePath String texture path for this butterfly e.g. "forestry:textures/entities/butterfly/..."
	 * @param dominant Whether this species is genetically dominant (false means it is recessive)
	 * @param branch Classification of this species
	 * @param binomial Binomial name of the species sans genus. "humboldti" will have the bee species flavour name be "Apis humboldti". Feel free to use fun names or null.
	 * @param serumColour The color of this butterfly's serum.
	 * @return a new butterfly species allele.
	 */
	IAlleleButterflySpeciesCustom createSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, String texturePath, boolean dominant, IClassification branch, String binomial, Color serumColour);
}
