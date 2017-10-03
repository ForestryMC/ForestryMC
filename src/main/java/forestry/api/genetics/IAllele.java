/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

/**
 * An {@link IIndividual}'s {@link IGenome} is composed of {@link IChromosome}s consisting each of a primary and secondary {@link IAllele}.
 * <p>
 * {@link IAllele}s hold all information regarding an {@link IIndividual}'s traits, from species to size, temperature tolerances, etc.
 * <p>
 * Should be extended for different types of alleles. ISpeciesAllele, IBiomeAllele, etc.
 *
 * @author SirSengir
 */
public interface IAllele {

	/**
	 * @return A unique string identifier for this allele.
	 */
	String getUID();

	/**
	 * @return modId of the mod that created this allele
	 * @since Forestry 5.6.0
	 */
	default String getModID() {
		return "forestry";
	}

	/**
	 * @return true if the allele is dominant, false otherwise.
	 */
	boolean isDominant();

	/**
	 * @return Localized short, human-readable identifier used in tooltips and beealyzer.
	 * @deprecated since Forestry 5.3.4. Use {@link #getAlleleName()}.
	 */
	@Deprecated
	String getName();

	/**
	 * @return Localized short, human-readable identifier used in tooltips and beealyzer.
	 * @since Forestry 5.3.4
	 * @apiNote This can't be named "getName" or it can conflict during obfuscation. https://github.com/md-5/SpecialSource/issues/12
	 */
	default String getAlleleName() {
		return getName();
	}

	/**
	 * @return The unlocalized identifier
	 */
	String getUnlocalizedName();

}
