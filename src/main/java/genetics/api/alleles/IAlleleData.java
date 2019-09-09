package genetics.api.alleles;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import net.minecraftforge.fml.ModLoadingContext;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IChromosomeType;

/**
 * The IAlleleData is a help interface that provides all information that is required to register a allele at the
 * {@link IAlleleRegistry} using {@link IAlleleRegistry#registerAllele(IAlleleData, IChromosomeType...)}.
 * <p>
 * If you implement this interface on an enum you can register every enum value with
 * {@link IAlleleRegistry#registerAlleles(IAlleleData[], IChromosomeType...)}.
 *
 * @param <V> The type of the value that this constant provides.
 */
public interface IAlleleData<V> extends IAlleleProvider {

	/**
	 * @return The value of the allele.
	 */
	V getValue();

	/**
	 * @return The dominance of the allele.
	 */
	boolean isDominant();

	/**
	 * @return The category of the allele is used for custom localisation and the registration name of the allele.
	 */
	String getCategory();

	/**
	 * @return The name that is used for the unlocalized name and the registration name of the allele.
	 */
	String getName();

	default Optional<IAlleleValue<V>> getAlleleValue() {
		return GeneticsAPI.apiInstance.getAlleleHelper().getAllele(this);
	}

	default IAlleleValue<V> createAllele() {
		return new AlleleCategorizedValue<>(ModLoadingContext.get().getActiveContainer().getModId(), getCategory(), getName(), getValue(), isDominant());
	}

	@Override
	default IAllele getAllele() {
		Optional<IAlleleValue<V>> optionalAllele = getAlleleValue();
		if (!optionalAllele.isPresent()) {
			throw new IllegalStateException("Attempted to get the allele from an allele data that was not registered! Please register the allele data before you use it.");
		}
		return optionalAllele.get();
	}

	default Collection<IChromosomeType> getTypes() {
		Optional<IAlleleValue<V>> optionalAllele = getAlleleValue();
		if (!optionalAllele.isPresent()) {
			return Collections.emptySet();
		}
		IAlleleValue<V> alleleValue = optionalAllele.get();
		return GeneticsAPI.apiInstance.getAlleleRegistry().getChromosomeTypes(alleleValue);
	}
}
