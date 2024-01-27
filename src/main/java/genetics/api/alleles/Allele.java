package genetics.api.alleles;

import com.google.common.base.MoreObjects;

import java.util.Objects;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A default implementation of a simple allele.
 */
public class Allele implements IAllele {
	public static final IAllele EMPTY = new Allele("empty", false).setRegistryName(new ResourceLocation("genetics", "empty"));

	private ResourceLocation registryName;
	protected final boolean dominant;
	protected final String localisationKey;

	public Allele(String localisationKey, boolean dominant) {
		this.localisationKey = localisationKey;
		this.dominant = dominant;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	public Allele setRegistryName(ResourceLocation registryName) {
		this.registryName = registryName;
		return this;
	}

	@Override
	public boolean isDominant() {
		return dominant;
	}

	@Override
	public int hashCode() {
		return getRegistryName() != null ? getRegistryName().hashCode() : Objects.hash(dominant);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(getLocalisationKey());
	}

	@Override
	public String getLocalisationKey() {
		return localisationKey;
	}

	@Override
	public IAlleleType getType() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IAllele otherAllele)) {
			return false;
		}
		return getRegistryName() != null ?
				getRegistryName().equals(((IAllele) obj).getRegistryName()) :
				dominant == otherAllele.isDominant();
	}

	@Override
	public String toString() {
		return MoreObjects
				.toStringHelper(this)
				.add("name", getRegistryName())
				.add("dominant", dominant)
				.add("key", localisationKey)
				.toString();
	}
}
