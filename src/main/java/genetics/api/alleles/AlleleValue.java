package genetics.api.alleles;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * A default implementation of an allele that provides a value.
 *
 * @param <V> the type of value that this allele contains.
 */
public class AlleleValue<V> extends Allele implements IAlleleValue<V> {
	protected final V value;

	public AlleleValue(String unlocalizedName, boolean dominant, V value) {
		super(unlocalizedName, dominant);
		this.value = value;
	}

	@Override
	public final V getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return getRegistryName() != null ? getRegistryName().hashCode() : Objects.hash(value, dominant);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IAlleleValue)) {
			return false;
		}
		IAlleleValue otherAllele = (IAlleleValue) obj;
		return getRegistryName() != null ?
			getRegistryName().equals(((IAllele) obj).getRegistryName()) :
			Objects.equals(value, otherAllele.getValue()) && dominant == otherAllele.isDominant();
	}

	@Override
	public String toString() {
		return MoreObjects
			.toStringHelper(this)
			.add("name", getRegistryName())
			.add("value", value)
			.add("dominant", dominant)
			.add("unloc", getLocalisationKey())
			.toString();
	}
}
