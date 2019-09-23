package genetics.utils;

import javax.annotation.Nullable;
import java.util.Optional;

import net.minecraft.util.ResourceLocation;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;

public class AlleleUtils {

	private AlleleUtils() {
	}

	@Nullable
	public static <A extends IAllele> A getAllele(String registryName) {
		return getAllele(new ResourceLocation(registryName));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <A extends IAllele> A getAllele(ResourceLocation registryName) {
		Optional<IAllele> optional = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(registryName);
		return (A) optional.orElse(null);
	}

	public static <A extends IAllele> A getAllele(String registryName, A fallback) {
		return getAllele(new ResourceLocation(registryName), fallback);
	}

	@SuppressWarnings("unchecked")
	public static <A extends IAllele> A getAllele(ResourceLocation registryName, A fallback) {
		Optional<IAllele> optional = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(registryName);
		return optional.map(allele -> (A) allele).orElse(fallback);
	}

	/**
	 * @return The value of the allele if the allele is an instance of {@link IAlleleValue} and not null.
	 * Otherwise it returns the given fallback object.
	 */
	public static <V> V getAlleleValue(IAllele allele, Class<? extends V> valueClass, V fallback) {
		if (!(allele instanceof IAlleleValue)) {
			return fallback;
		}
		IAlleleValue alleleValue = (IAlleleValue) allele;
		Object value = alleleValue.getValue();
		if (valueClass.isInstance(value)) {
			return valueClass.cast(value);
		}
		return fallback;
	}

	/**
	 * @return The value of the allele if the allele is an instance of {@link IAlleleValue} and not null.
	 * Otherwise it returns null.
	 */
	@Nullable
	public static <V> V getAlleleValue(IAllele allele, Class<? extends V> valueClass) {
		if (!(allele instanceof IAlleleValue)) {
			return null;
		}
		IAlleleValue alleleValue = (IAlleleValue) allele;
		Object value = alleleValue.getValue();
		if (valueClass.isInstance(value)) {
			return valueClass.cast(value);
		}
		return null;
	}
}
