/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import java.awt.Color;

import forestry.api.genetics.IClassification;

public interface IButterflyFactory {
	/**
	 * Creates a new butterfly species.
	 * Automatically registered with AlleleManager.alleleRegistry.registerAllele()
	 * See IAlleleButterflySpeciesBuilder and IAlleleSpeciesBuilder for adding additional properties to the returned species.
	 *
	 * @param uid                    Unique Identifier for this species
	 * @param unlocalizedName        Unlocalized name for this species
	 * @param authority              Authority for the binomial name, e.g. "Sengir" on species of base Forestry.
	 * @param unlocalizedDescription Unlocalized description for this species
	 * @param modID                  The modID form the mod of the butterfly
	 * @param texturePath            String texture path for this butterfly e.g. "forestry:butterfly/..."
	 * @param dominant               Whether this species is genetically dominant (false means it is recessive)
	 * @param branch                 Classification of this species
	 * @param binomial               Binomial name of the species sans genus. "humboldti" will have the bee species flavour name be "Apis humboldti". Feel free to use fun names or null.
	 * @param serumColour            The color of this butterfly's serum.
	 * @return a new butterfly species allele.
	 */
	IAlleleButterflySpeciesBuilder createSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, String modID, String texturePath, boolean dominant, IClassification branch, String binomial, Color serumColour);
}
