/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.awt.Color;

import forestry.api.genetics.IClassification;

public interface ITreeFactory {
	/**
	 * Creates a new tree species.
	 * Automatically registered with AlleleManager.alleleRegistry.registerAllele()
	 * See IAlleleTreeSpeciesCustom and IAlleleSpeciesCustom for adding additional properties to the returned species.
	 *
	 * @param uid Unique Identifier for this species
	 * @param unlocalizedName Unlocalized name for this species
	 * @param authority Authority for the binomial name, e.g. "Sengir" on species of base Forestry.
	 * @param unlocalizedDescription Unlocalized description for this species
	 * @param dominant Whether this species is genetically dominant (false means it is recessive)
	 * @param branch Classification of this species
	 * @param binomial Binomial name of the species sans genus. "humboldti" will have the bee species flavour name be "Apis humboldti". Feel free to use fun names or null.
	 * @param leafIconProvider The leaf icon provider for this species
	 * @param germlingIconProvider The germling icon provider for this species
	 * @param generator The tree generator
	 * @return a new tree species allele.
	 */
	IAlleleTreeSpeciesCustom createSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean dominant, IClassification branch, String binomial, ILeafIconProvider leafIconProvider, IGermlingIconProvider germlingIconProvider, ITreeGenerator generator);

	/** Get one of the built-in Forestry leaf types. Default type is deciduous. */
	ILeafIconProvider getLeafIconProvider(EnumLeafType enumLeafType, Color color, Color colorPollinated);
}
