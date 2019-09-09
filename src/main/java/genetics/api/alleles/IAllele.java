package genetics.api.alleles;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import net.minecraftforge.registries.IForgeRegistryEntry;

import genetics.api.individual.IChromosome;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;

/**
 * An {@link IIndividual}'s {@link IGenome} is composed of {@link IChromosome}s consisting each of a active and inactive {@link IAllele}.
 * <p>
 * {@link IAllele}s hold all information regarding an {@link IIndividual}'s traits, from species to size, temperature tolerances, etc.
 * <p>
 * {@link IAlleleValue} with its default implementations {@link AlleleValue} and {@link AlleleCategorizedValue} can be used
 * if you want to create a {@link IAllele} that only represents a simple value object.
 */
public interface IAllele extends IForgeRegistryEntry<IAllele> {
	/**
	 * @return true if the allele is dominant, false otherwise.
	 */
	boolean isDominant();

	/**
	 * @return Localized short, human-readable identifier used in tooltips and beealyzer.
	 */
	ITextComponent getDisplayName();

	/**
	 * @return The localisation identifier for this allele.
	 */
	String getLocalisationKey();

	/**
	 * @return The registry name
	 */
	@Nonnull
	ResourceLocation getRegistryName();

	/**
	 * @return The type that was used to deserialize this allele.
	 */
	IAlleleType getType();
}
