/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

/**
 * Describes a class of species (i.e. bees, trees, butterflies), provides helper functions and access to common functionality.
 */
public interface ISpeciesRoot<C extends IChromosomeType> {

	/**
	 * @return A unique identifier for the species class. Should consist of "root" + a common name for the species class in camel-case, i.e. "rootBees", "rootTrees", "rootButterflies".
	 */
	String getUID();

	/**
	 * @return Class of the sub-interface inheriting from {@link IIndividual}.
	 */
	Class<? extends IIndividual<C>> getMemberClass();

	/**
	 * @return Integer denoting the number of (counted) species of this type in the world.
	 */
	int getSpeciesCount();

	/**
	 * Used to check whether a given itemstack contains genetic data corresponding to an {@link IIndividual} of this class.
	 * @param stack itemstack to check.
	 * @return true if the itemstack contains an {@link IIndividual} of this class, false otherwise.
	 */
	boolean isMember(ItemStack stack);

	/**
	 * Used to check whether a given itemstack contains genetic data corresponding to an {@link IIndividual} of this class and matches the given type.
	 * @param stack itemstack to check.
	 * @param type Integer denoting the type needed to match. (i.e. butterfly vs. butterfly serum; bee queens, princesses, drones; etc.)
	 * @return true if the itemstack contains an {@link IIndividual} of this class, false otherwise.
	 */
	boolean isMember(ItemStack stack, int type);

	/**
	 * Used to check whether the given {@link IIndividual} is member of this class.
	 * @param individual {@link IIndividual} to check.
	 * @return true if the individual is member of this class, false otherwise.
	 */
	boolean isMember(IIndividual individual);

	@Nullable
	IIndividual<C> getMember(ItemStack stack);

	@Nullable
	IIndividual<C> getMember(NBTTagCompound compound);

	@Nullable
	ISpeciesType getType(ItemStack itemStack);

	ItemStack getMemberStack(IIndividual<C> individual, int type);

	/* BREEDING TRACKER */
	@Nonnull
	IBreedingTracker<C> getBreedingTracker(@Nonnull World world, @Nonnull GameProfile player);

	/* GENOME MANIPULATION */
	@Nonnull
	IIndividual<C> templateAsIndividual(ImmutableMap<C, IAllele> template);

	@Nonnull
	IIndividual<C> templateAsIndividual(ImmutableMap<C, IAllele> templateActive, ImmutableMap<C, IAllele> templateInactive);

	ImmutableMap<C, IChromosome> templateAsChromosomes(ImmutableMap<C, IAllele> template);

	ImmutableMap<C, IChromosome> templateAsChromosomes(ImmutableMap<C, IAllele> templateActive, ImmutableMap<C, IAllele> templateInactive);

	IGenome<C> templateAsGenome(ImmutableMap<C, IAllele> template);

	IGenome<C> templateAsGenome(ImmutableMap<C, IAllele> templateActive, ImmutableMap<C, IAllele> templateInactive);

	@Nonnull
	IGenome<C> chromosomesAsGenome(ImmutableMap<C, IChromosome> chromosomes);

	/* TEMPLATES */

	/**
	 * Registers a bee template using the UID of the first allele as identifier.
	 *
	 * @param template
	 */
	void registerTemplate(ImmutableMap<C, IAllele> template);

	/**
	 * Registers a bee template using the passed identifier.
	 *
	 * @param template
	 */
	void registerTemplate(String identifier, ImmutableMap<C, IAllele> template);

	/**
	 * Retrieves a registered template using the passed identifier.
	 *
	 * @param identifier
	 * @return Map of {@link IAllele} representing a genome.
	 */
	@Nullable
	ImmutableMap<C, IAllele> getTemplate(String identifier);

	/**
	 * @return Default individual template for use when stuff breaks.
	 */
	ImmutableMap<C, IAllele> getDefaultTemplate();

	/**
	 * @param rand Random to use.
	 * @return A random template from the pool of registered species templates.
	 */
	ImmutableMap<C, IAllele> getRandomTemplate(Random rand);

	Map<String, ImmutableMap<C, IAllele>> getGenomeTemplates();

	List<? extends IIndividual<C>> getIndividualTemplates();

	/* MUTATIONS */

	/**
	 * Use to register mutations.
	 *
	 * @param mutation
	 */
	void registerMutation(IMutation<C> mutation);

	/**
	 * @return All registered mutations.
	 */
	Collection<? extends IMutation<C>> getMutations(boolean shuffle);

	/**
	 * @param other Allele to match mutations against.
	 * @return All registered mutations the given allele is part of.
	 */
	List<IMutation<C>> getCombinations(IAllele other);

	/**
	 * @since Forestry 3.7
	 * @return all possible mutations that result from breeding two species
	 */
	List<IMutation<C>> getCombinations(IAlleleSpecies<C> parentSpecies0, IAlleleSpecies<C> parentSpecies1, boolean shuffle);

	Collection<? extends IMutation<C>> getPaths(IAllele result, C chromosomeType);

	/* RESEARCH */

	/**
	 * @return List of generic catalysts which should be accepted for research by species of this class.
	 */
	Map<ItemStack, Float> getResearchCatalysts();

	/**
	 * Sets an item stack as a valid (generic) research catalyst for this class.
	 * @param itemstack ItemStack to set as suitable.
	 * @param suitability Float between 0 and 1 to indicate suitability.
	 */
	void setResearchSuitability(ItemStack itemstack, float suitability);

	/**
	 * @return Array of {@link IChromosomeType} which are in this species genome
	 */
	@Nonnull
	C[] getKaryotype();

	/**
	 * @return {@link IChromosomeType} which is the "key" for this species class, usually the species chromosome.
	 */
	@Nonnull
	C getKaryotypeKey();

	C getChromosomeTypeForUid(byte uid);

	/* GAME MODE */
	void resetMode();

	@Nonnull
	List<? extends ISpeciesMode<C>> getModes();

	@Nonnull
	ISpeciesMode<C> getMode(@Nonnull World world);

	@Nonnull
	ISpeciesMode<C> getMode(@Nonnull String name);

	void setMode(@Nonnull World world, @Nonnull String name);

}
