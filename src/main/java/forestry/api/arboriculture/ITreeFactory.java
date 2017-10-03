/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;
import java.awt.Color;

import forestry.api.genetics.IClassification;

public interface ITreeFactory {
	/**
	 * Creates a new tree species.
	 * Automatically registered with AlleleManager.alleleRegistry.registerAllele()
	 * See IAlleleTreeSpeciesBuilder and IAlleleSpeciesBuilder for adding additional properties to the returned species.
	 *
	 * @param uid Unique Identifier for this species
	 * @param unlocalizedName Unlocalized name for this species
	 * @param authority Authority for the binomial name, e.g. "Sengir" on species of base Forestry.
	 * @param unlocalizedDescription Unlocalized description for this species
	 * @param dominant Whether this species is genetically dominant (false means it is recessive)
	 * @param branch Classification of this species
	 * @param binomial Binomial name of the species sans genus. "humboldti" will have the bee species flavour name be "Apis humboldti". Feel free to use fun names or null.
	 * @param modID The modID from the mod of the species
	 * @param leafSpriteProvider The leaf sprite provider for this species
	 * @param germlingModelProvider The germling model provider for this species
	 * @param woodProvider The wood texture provider. It is used to get wood textures for the charcoal pile.
	 * @param generator The tree generator
	 * @param leafProvider The leaf provider
	 * @return a new tree species allele.
	 */
	IAlleleTreeSpeciesBuilder createSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean dominant, IClassification branch, String binomial, String modID, ILeafSpriteProvider leafSpriteProvider, IGermlingModelProvider germlingModelProvider, IWoodProvider woodProvider, ITreeGenerator generator, @Nullable ILeafProvider leafProvider);

	/**
	 * @deprecated since Forestry 5.6.0. use {@link #createSpecies(String, String, String, String, boolean, IClassification, String, String, ILeafSpriteProvider, IGermlingModelProvider, IWoodProvider, ITreeGenerator, ILeafProvider)}
	 */
	@Deprecated
	IAlleleTreeSpeciesBuilder createSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean dominant, IClassification branch, String binomial, String modID, ILeafSpriteProvider leafSpriteProvider, IGermlingModelProvider germlingModelProvider, IWoodProvider woodProvider, ITreeGenerator generator);


	/**
	 * Get one of the built-in Forestry leaf types. Default type is deciduous.
	 */
	ILeafSpriteProvider getLeafIconProvider(EnumLeafType enumLeafType, Color color, Color colorPollinated);
}
