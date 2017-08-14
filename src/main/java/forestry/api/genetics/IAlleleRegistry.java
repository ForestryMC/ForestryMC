/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

import com.mojang.authlib.GameProfile;
import forestry.api.genetics.IClassification.EnumClassLevel;
import net.minecraft.item.ItemStack;

/**
 * Manages {@link ISpeciesRoot}, {@link IAllele}s, {@link IFruitFamily}s, {@link IClassification}, the blacklist and allows creation of research notes.
 *
 * @author SirSengir
 */
public interface IAlleleRegistry {

	/* SPECIES ROOT CLASSES */

	/**
	 * Register a {@link ISpeciesRoot}.
	 *
	 * @param root {@link ISpeciesRoot} to register.
	 */
	void registerSpeciesRoot(ISpeciesRoot root);

	/**
	 * @return Map of all registered {@link ISpeciesRoot}.
	 */
	Map<String, ISpeciesRoot> getSpeciesRoot();

	/**
	 * Retrieve the {@link ISpeciesRoot} with the given uid.
	 *
	 * @param uid Unique id for the species class, i.e. "rootBees", "rootTrees", "rootButterflies".
	 * @return {@link ISpeciesRoot} if it exists, null otherwise.
	 */
	@Nullable
	ISpeciesRoot getSpeciesRoot(String uid);

	/**
	 * Retrieve a matching {@link ISpeciesRoot} for the given itemstack.
	 *
	 * @param stack An itemstack possibly containing NBT data which can be converted by a species root.
	 * @return {@link ISpeciesRoot} if found, null otherwise.
	 */
	@Nullable
	ISpeciesRoot getSpeciesRoot(ItemStack stack);

	/**
	 * Retrieve a matching {@link ISpeciesRoot} for the given {@link IIndividual}-class.
	 *
	 * @param individualClass Class extending {@link IIndividual}.
	 * @return {@link ISpeciesRoot} if found, null otherwise.
	 */
	@Nullable
	ISpeciesRoot getSpeciesRoot(Class<? extends IIndividual> individualClass);

	/**
	 * Retrieve a matching {@link ISpeciesRoot} for the given {@link IIndividual}
	 */
	ISpeciesRoot getSpeciesRoot(IIndividual individual);

	/* INDIVIDUAL */

	/**
	 * Tests the itemstack for genetic information.
	 *
	 * @return true if the itemstack is an individual.
	 */
	boolean isIndividual(ItemStack stack);

	/**
	 * Retrieve genetic information from an itemstack.
	 *
	 * @param stack Stack to retrieve genetic information for.
	 * @return IIndividual containing genetic information, null if none could be extracted.
	 */
	@Nullable
	IIndividual getIndividual(ItemStack stack);

	/* ALLELES */

	/**
	 * @return HashMap of all currently registered alleles.
	 */
	Map<String, IAllele> getRegisteredAlleles();

	/**
	 * @return unmodifiable collection of all the known allele variations for the given chromosome type.
	 * @since Forestry 4.2
	 */
	Collection<IAllele> getRegisteredAlleles(IChromosomeType type);

	/**
	 * Registers an allele.
	 * <p>
	 * NOTE: Where possible, it is recommended to use IAlleleFactory instead
	 * because it has built-in advanced localization support.
	 *
	 * @param allele          IAllele to register.
	 * @param chromosomeTypes valid chromosomeTypes for this allele.
	 * @since Forestry 4.2
	 */
	void registerAllele(IAllele allele, IChromosomeType... chromosomeTypes);

	/**
	 * Add more valid chromosome types for an allele.
	 * Used by addons that create new chromosome types beyond bees, trees, and butterflies.
	 * @since Forestry 5.3.1
	 */
	void addValidAlleleTypes(IAllele allele, IChromosomeType... chromosomeTypes);

	/**
	 * @return HashMap of all registered deprecated alleles and their corresponding replacements
	 */
	Map<String, IAllele> getDeprecatedAlleleReplacements();

	/**
	 * Registers an old allele UID and the new IAllele to replace instances of it with.
	 *
	 * @param deprecatedAlleleUID the old allele's UID
	 * @param replacement         the IAllele that the deprecated Allele will be replaced with.
	 */
	void registerDeprecatedAlleleReplacement(String deprecatedAlleleUID, IAllele replacement);

	/**
	 * Gets an allele
	 *
	 * @param uid String based unique identifier of the allele to retrieve.
	 * @return IAllele if found or a replacement is found in the Deprecated Allele map, null otherwise.
	 */
	@Nullable
	IAllele getAllele(String uid);

	/**
	 * @return unmodifiable collection of all the known chromosome types.
	 * @since Forestry 5.0
	 */
	Collection<IChromosomeType> getChromosomeTypes(IAllele allele);

	/* CLASSIFICATIONS */

	/**
	 * @return HashMap of all currently registered classifications.
	 */
	Map<String, IClassification> getRegisteredClassifications();

	/**
	 * Registers a classification.
	 *
	 * @param classification IClassification to register.
	 */
	void registerClassification(IClassification classification);

	/**
	 * Creates and returns a classification.
	 *
	 * @param level      EnumClassLevel of the classification to create.
	 * @param uid        String based unique identifier. Implementation will throw an exception if the key is already taken.
	 * @param scientific Binomial for the given classification.
	 * @return Created {@link IClassification} for easier chaining.
	 */
	IClassification createAndRegisterClassification(EnumClassLevel level, String uid, String scientific);

	IClassification createAndRegisterClassification(EnumClassLevel level, String uid, String scientific, IClassification... members);

	/**
	 * Gets a classification.
	 *
	 * @param uid String based unique identifier of the classification to retrieve.
	 * @return {@link IClassification} if found, null otherwise.
	 */
	IClassification getClassification(String uid);

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
	
	/* BLACKLIST */

	/**
	 * Blacklist an allele identified by its UID from mutation.
	 *
	 * @param uid UID of the allele to blacklist.
	 */
	void blacklistAllele(String uid);

	/**
	 * @return Current blacklisted alleles.
	 */
	Collection<String> getAlleleBlacklist();

	/**
	 * @param uid UID of the species to vet.
	 * @return true if the allele is blacklisted.
	 */
	boolean isBlacklisted(String uid);

	/* RESEARCH */

	/**
	 * @param researcher Username of the player who researched this note.
	 * @param species    {@link IAlleleSpecies} to encode on the research note.
	 * @return An itemstack containing a research note with the given species encoded onto it.
	 */
	ItemStack getSpeciesNoteStack(GameProfile researcher, IAlleleSpecies species);

	/**
	 * @param researcher Username of the player who researched this note.
	 * @param mutation   {@link IMutation} to encode on the research note.
	 * @return An itemstack containing a research note with the given mutation encoded onto it.
	 */
	ItemStack getMutationNoteStack(GameProfile researcher, IMutation mutation);

}
