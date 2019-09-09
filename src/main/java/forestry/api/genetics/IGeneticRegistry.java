/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.Map;

import net.minecraft.item.ItemStack;

import com.mojang.authlib.GameProfile;

import genetics.api.alleles.IAllele;
import genetics.api.classification.IClassification;
import genetics.api.mutation.IMutation;

/**
 * Manages {@link IForestrySpeciesRoot}, {@link IAllele}s, {@link IFruitFamily}s, {@link IClassification}, the blacklist and allows creation of research notes.
 *
 * @author SirSengir
 */
public interface IGeneticRegistry {

	/* FRUIT FAMILIES */

	/**
	 * Get all registered fruit families.
	 *
	 * @return A map of registered fruit families and their UIDs.
	 */
	Map<String, IFruitFamily> getRegisteredFruitFamilies();

	/**
	 * Registers a new fruit family.
	 */
	void registerFruitFamily(IFruitFamily family);

	/**
	 * Retrieves a fruit family identified by uid.
	 *
	 * @return {IFruitFamily} if found, false otherwise.
	 */
	IFruitFamily getFruitFamily(String uid);

	/* ALLELE HANDLERS */

	/**
	 * Registers a new IAlleleHandler
	 *
	 * @param handler IAlleleHandler to register.
	 */
	void registerAlleleHandler(IAlleleHandler handler);

	/* RESEARCH */

	/**
	 * @param researcher Username of the player who researched this note.
	 * @param species    {@link IAlleleForestrySpecies} to encode on the research note.
	 * @return An itemstack containing a research note with the given species encoded onto it.
	 */
	ItemStack getSpeciesNoteStack(GameProfile researcher, IAlleleForestrySpecies species);

	/**
	 * @param researcher Username of the player who researched this note.
	 * @param mutation   {@link IMutation} to encode on the research note.
	 * @return An itemstack containing a research note with the given mutation encoded onto it.
	 */
	ItemStack getMutationNoteStack(GameProfile researcher, IMutation mutation);

}
