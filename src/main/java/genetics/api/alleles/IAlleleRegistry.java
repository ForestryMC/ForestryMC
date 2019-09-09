package genetics.api.alleles;

import java.util.Collection;
import java.util.Optional;

import net.minecraft.util.ResourceLocation;

import genetics.api.IGeneticApiInstance;
import genetics.api.IGeneticPlugin;
import genetics.api.individual.IChromosomeType;

/**
 * The {@link IAlleleRegistry} offers several functions for registering and retrieving alleles.
 * <p>
 * The IAlleleRegistry instance is passed to your genetic plugin in {@link IGeneticPlugin#registerAlleles(IAlleleRegistry)}}.
 * Later you can get the instance from {@link IGeneticApiInstance#getAlleleRegistry()}.
 */
public interface IAlleleRegistry {
	/**
	 * Creates a allele with the data that the {@link IAlleleData} contains and registers it with the given chromosome
	 * types.
	 *
	 * @param value An object that contains all information that is needed to create a allele with an specific value.
	 * @param types chromosome types for the allele.
	 * @return Returns the created and registered allele.
	 */
	<V> IAlleleValue<V> registerAllele(IAlleleData<V> value, IChromosomeType... types);

	/**
	 * Creates a allele for every entry in the given array. All alleles will be registered with the given chromosome
	 * types.
	 *
	 * @param values A array that contains all the data for the alleles that should be created.
	 * @param types  All chromosomes types with that the alleles should be registered.
	 * @return A array that contains all created and registered alleles. With the same index of their data in the given
	 * array.
	 */
	<V> IAlleleValue<V>[] registerAlleles(IAlleleData<V>[] values, IChromosomeType... types);

	/**
	 * Creates and registers an allele that contains the given value and has the given dominant state if no allele with
	 * the value and the given dominant state exists, otherwise it adds the types to the existing {@link IAllele}.
	 *
	 * @param category  The category is used for custom localisation and the registration name.
	 * @param valueName The category is used for custom localisation and the registration name.
	 * @param value     the value of the allele
	 * @param dominant  if true the allele is dominant, otherwise the allele is recessive.
	 * @param types     chromosome types for this allele.
	 */
	<V> IAlleleValue<V> registerAllele(String category, String valueName, V value, boolean dominant, IChromosomeType... types);

	/**
	 * Registers an allele.
	 *
	 * @param allele IAllele to register.
	 * @param types  allele keys for this allele.
	 */
	<A extends IAllele> A registerAllele(A allele, IChromosomeType... types);

	/**
	 * Add more valid chromosome types for an allele.
	 * Used by addons that create new chromosome types beyond bees, trees, and butterflies.
	 */
	default IAlleleRegistry addValidAlleleTypes(String registryName, IChromosomeType... types) {
		return addValidAlleleTypes(new ResourceLocation(registryName), types);
	}

	/**
	 * Add more valid chromosome types for an allele.
	 * Used by addons that create new chromosome types beyond bees, trees, and butterflies.
	 */
	IAlleleRegistry addValidAlleleTypes(ResourceLocation registryName, IChromosomeType... types);

	/**
	 * Add more valid chromosome types for an allele.
	 * Used by addons that create new chromosome types beyond bees, trees, and butterflies.
	 */
	IAlleleRegistry addValidAlleleTypes(IAllele allele, IChromosomeType... types);

	/**
	 * @return The default allele that will be used insteadof null for every allele.
	 */
	IAllele getDefaultAllele();

	ResourceLocation getDefaultKey();

	/**
	 * Gets an allele
	 *
	 * @param registryName The registry name of the allele to retrieve as a {@link String}.
	 * @return A optional that contains the IAllele if found, a empty optional otherwise.
	 */
	default Optional<IAllele> getAllele(String registryName) {
		return getAllele(new ResourceLocation(registryName));
	}

	/**
	 * Gets an allele
	 *
	 * @param registryName The registry name of the allele to retrieve as a {@link ResourceLocation}.
	 * @return A optional that contains the IAllele if found, a empty optional otherwise.
	 */
	Optional<IAllele> getAllele(ResourceLocation registryName);

	/**
	 * @return unmodifiable collection of all the known chromosome types.
	 */
	Collection<IChromosomeType> getChromosomeTypes(IAllele allele);

	/**
	 * @return unmodifiable collection of all the known allele variations for the given chromosome type.
	 */
	Collection<IAllele> getRegisteredAlleles(IChromosomeType type);

	Collection<IAllele> getRegisteredAlleles();

	Collection<ResourceLocation> getRegisteredNames();

	/**
	 * Returns true if the given allele is a valid allele for the given chromosome type.
	 *
	 * @param allele The allele to test.
	 * @param type   The chromosome type.
	 * @return True if the given allele is a valid allele for the given chromosome type, false otherwise.
	 */
	boolean isValidAllele(IAllele allele, IChromosomeType type);

	/* ALLELE HANDLERS */

	/**
	 * Registers a new IAlleleHandler
	 *
	 * @param handler IAlleleHandler to register.
	 */
	void registerHandler(IAlleleHandler handler);

	/**
	 * @return all handlers that were registered.
	 */
	Collection<IAlleleHandler> getHandlers();

	/* BLACKLIST */

	/**
	 * Blacklist an allele identified by its UID from mutation.
	 *
	 * @param registryName UID of the allele to blacklist.
	 */
	void blacklistAllele(String registryName);

	default void blacklistAllele(ResourceLocation registryName) {
		blacklistAllele(registryName.toString());
	}

	/**
	 * @return Current blacklisted alleles.
	 */
	Collection<String> getAlleleBlacklist();

	/**
	 * @param registryName UID of the species to vet.
	 * @return true if the allele is blacklisted.
	 */
	boolean isBlacklisted(String registryName);

	default boolean isBlacklisted(ResourceLocation registryName) {
		return isBlacklisted(registryName.toString());
	}

	default boolean isBlacklisted(IAllele allele) {
		return isBlacklisted(allele.getRegistryName());
	}
}
