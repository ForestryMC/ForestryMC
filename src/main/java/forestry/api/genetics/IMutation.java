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

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.INbtWritable;

/**
 * Individuals can be seeded either as hive drops or as mutation results.
 *
 * {@link IAlleleRegistry} manages these.
 *
 * @author SirSengir
 */
public interface IMutation<C extends IChromosomeType> extends INbtWritable {

	/**
	 * @return {@link ISpeciesRoot} this mutation is associated with.
	 */
	@Nonnull
	ISpeciesRoot<C> getRoot();

	/**
	 * @return first of the alleles implementing IAlleleSpecies required for this mutation.
	 */
	@Nonnull
	IAlleleSpecies<C> getSpecies0();

	/**
	 * @return second of the alleles implementing IAlleleSpecies required for this mutation.
	 */
	@Nonnull
	IAlleleSpecies<C> getSpecies1();

	/**
	 * @return The full default genome of the mutated side.
	 */
	@Nonnull
	ImmutableMap<C, IAllele> getResultTemplate();

	/**
	 * @return Unmodified base chance for mutation to fire.
	 */
	float getBaseChance();

	float getChance(World world, BlockPos pos, IAlleleSpecies<C> species0, IAlleleSpecies<C> species1, IGenome<C> genome0, IGenome<C> genome1);

	/**
	 * @return Collection of localized, human-readable strings describing special mutation conditions, if any. 
	 */
	@Nonnull
	Collection<String> getSpecialConditions();
	
	/**
	 * @param allele
	 * @return true if the passed allele is one of the alleles participating in this mutation.
	 */
	boolean isPartner(IAllele allele);

	/**
	 * @param allele
	 * @return the other allele which was not passed as argument.
	 */
	@Nullable
	IAllele getPartner(IAllele allele);

	/**
	 * @return true if the mutation should not be displayed in the beealyzer.
	 */
	boolean isSecret();

}
