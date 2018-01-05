/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Describes a class of species (i.e. bees, trees, butterflies), provides helper functions and access to common functionality.
 */
public interface ISpeciesRoot {

	/**
	 * @return A unique identifier for the species class. Should consist of "root" + a common name for the species class in camel-case, i.e. "rootBees", "rootTrees", "rootButterflies".
	 */
	String getUID();

	/**
	 * @return Class of the sub-interface inheriting from {@link IIndividual}.
	 */
	Class<? extends IIndividual> getMemberClass();

	/**
	 * @return Integer denoting the number of (counted) species of this type in the world.
	 */
	int getSpeciesCount();

	/**
	 * Used to check whether a given itemstack contains genetic data corresponding to an {@link IIndividual} of this class.
	 *
	 * @param stack itemstack to check.
	 * @return true if the itemstack contains an {@link IIndividual} of this class, false otherwise.
	 */
	boolean isMember(ItemStack stack);

	/**
	 * Used to check whether a given itemstack contains genetic data corresponding to an {@link IIndividual} of this class and matches the given type.
	 *
	 * @param stack itemstack to check.
	 * @param type  Integer denoting the type needed to match. (i.e. butterfly vs. butterfly serum; bee queens, princesses, drones; etc.)
	 * @return true if the itemstack contains an {@link IIndividual} of this class, false otherwise.
	 */
	boolean isMember(ItemStack stack, ISpeciesType type);

	/**
	 * Used to check whether the given {@link IIndividual} is member of this class.
	 *
	 * @param individual {@link IIndividual} to check.
	 * @return true if the individual is member of this class, false otherwise.
	 */
	boolean isMember(IIndividual individual);

	@Nullable
	IIndividual getMember(ItemStack stack);

	IIndividual getMember(NBTTagCompound compound);

	/**
	 * @param translatorKey The key of the translator the block of {@link IBlockState} that you want to translate
	 *                         with the translator.
	 * @param translator A translator that should be used to translate the data.
	 * @since Forestry 5.8
	 */
	void registerTranslator(Block translatorKey, IBlockTranslator translator);

	/**
	 * @param translatorKey The key of the translator it is the item of the {@link ItemStack} that you want to translate
	 *                      with the translator.
	 * @param translator A translator that should be used to translate the data.
	 * @since Forestry 5.8
	 */
	void registerTranslator(Item translatorKey, IItemTranslator translator);

	/**
	 * @param translatorKey The key of the translator, by default it is the item of the {@link ItemStack} that you want
	 *                      to translate with the translator.
	 *  @since Forestry 5.8
	 */
	@Nullable
	IItemTranslator getTranslator(Item translatorKey);

	/**
	 * @param translatorKey The key of the translator the block of the{@link IBlockState} that you want to translate
	 *                      with the translator.
	 *
	 * @since Forestry 5.8
	 */
	@Nullable
	IBlockTranslator getTranslator(Block translatorKey);

	/**
	 * Translates {@link IBlockState}s into genetic data.
	 *
	 * @since Forestry 5.8
	 */
	@Nullable
	<I extends IIndividual> I translateMember(IBlockState objectToTranslate);

	/**
	 * Translates {@link ItemStack}s into genetic data.
	 *
	 * @since Forestry 5.8
	 */
	@Nullable
	<I extends IIndividual> I translateMember(ItemStack objectToTranslate);

	/**
	 * Translates a {@link IBlockState}s into genetic data and returns a {@link ItemStack} that contains this data.
	 *
	 * @since Forestry 5.8
	 */
	ItemStack getGeneticEquivalent(IBlockState objectToTranslate);

	/**
	 * Translates {@link ItemStack}s into genetic data and returns a other {@link ItemStack} that contains this data.
	 *
	 * @since Forestry 5.8
	 */
	ItemStack getGeneticEquivalent(ItemStack objectToTranslate);

	@Nullable
	ISpeciesType getType(ItemStack itemStack);

	/**
	 * Species type used to represent this species in icons
	 */
	ISpeciesType getIconType();

	ItemStack getMemberStack(IIndividual individual, ISpeciesType type);

	/* BREEDING TRACKER */
	IBreedingTracker getBreedingTracker(World world, @Nullable GameProfile player);

	/* GENOME MANIPULATION */
	IIndividual templateAsIndividual(IAllele[] template);

	IIndividual templateAsIndividual(IAllele[] templateActive, IAllele[] templateInactive);

	IChromosome[] templateAsChromosomes(IAllele[] template);

	IChromosome[] templateAsChromosomes(IAllele[] templateActive, IAllele[] templateInactive);

	IGenome templateAsGenome(IAllele[] template);

	IGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive);

	/**
	 * @return A wrapped version of the genome.
	 *
	 * @since Forestry 5.8
	 */
	IGenomeWrapper getWrapper(IGenome genome);

	/* TEMPLATES */
	/**
	 * @since Forestry 5.8
	 */
	IAlleleTemplateBuilder createTemplateBuilder();

	/**
	 * @since Forestry 5.8
	 */
	IAlleleTemplateBuilder createTemplateBuilder(IAllele[] alleles);

	/**
	 * Registers a bee template using the UID of the first allele as identifier.
	 */
	void registerTemplate(IAllele[] template);

	/**
	 * Registers a bee template using the passed identifier.
	 */
	void registerTemplate(String identifier, IAllele[] template);

	/**
	 * Retrieves a registered template using the passed species unique identifier.
	 *
	 * @param identifier species UID
	 * @return Array of {@link IAllele} representing a genome.
	 */
	@Nullable
	IAllele[] getTemplate(String identifier);

	/**
	 * Retrieves a registered template using the passed species.
	 *
	 * @param species species
	 * @return Array of {@link IAllele} representing a genome.
	 */
	IAllele[] getTemplate(IAlleleSpecies species);

	/**
	 * @return Default individual template for use when stuff breaks.
	 */
	IAllele[] getDefaultTemplate();

	/**
	 * @param rand Random to use.
	 * @return A random template from the pool of registered species templates.
	 */
	IAllele[] getRandomTemplate(Random rand);

	/**
	 * @return A map with all genome templates arrays that where registered with {@link #registerTemplate(String, IAllele[])}.
	 */
	Map<String, IAllele[]> getGenomeTemplates();

	List<? extends IIndividual> getIndividualTemplates();

	/* MUTATIONS */
	/**
	 * Use to register mutations.
	 */
	void registerMutation(IMutation mutation);

	/**
	 * @return All registered mutations.
	 */
	List<? extends IMutation> getMutations(boolean shuffle);

	/**
	 * @param other Allele to match mutations against.
	 * @return All registered mutations the given allele is part of.
	 */
	List<? extends IMutation> getCombinations(IAllele other);

	/**
	 * @param other Allele to match mutations against.
	 * @return All registered mutations the give allele is resolute of.
	 */
	List<? extends IMutation> getResultantMutations(IAllele other);

	/**
	 * @return all possible mutations that result from breeding two species
	 * @since Forestry 3.7
	 */
	List<IMutation> getCombinations(IAlleleSpecies parentSpecies0, IAlleleSpecies parentSpecies1, boolean shuffle);

	Collection<? extends IMutation> getPaths(IAllele result, IChromosomeType chromosomeType);

	/* RESEARCH */

	/**
	 * @return List of generic catalysts which should be accepted for research by species of this class.
	 */
	Map<ItemStack, Float> getResearchCatalysts();

	/**
	 * Sets an item stack as a valid (generic) research catalyst for this class.
	 *
	 * @param itemstack   ItemStack to set as suitable.
	 * @param suitability Float between 0 and 1 to indicate suitability.
	 */
	void setResearchSuitability(ItemStack itemstack, float suitability);

	/**
	 * @return Array of all {@link IChromosomeType} which are in this species genome
	 */
	IChromosomeType[] getKaryotype();

	/**
	 * @return {@link IChromosomeType} which is the "key" for this species class, the species chromosome.
	 */
	IChromosomeType getSpeciesChromosomeType();

	/**
	 * Plugin to add information for the handheld genetic analyzer.
	 */
	IAlyzerPlugin getAlyzerPlugin();

	/**
	 * Plugin to add information for the handheld genetic analyzer and the database.
	 * @since Forestry 5.7
	 */
	@Nullable
	@SideOnly(Side.CLIENT)
	default ISpeciesPlugin getSpeciesPlugin(){
		return null;
	}
}
