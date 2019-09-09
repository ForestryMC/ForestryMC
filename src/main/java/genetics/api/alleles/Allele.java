package genetics.api.alleles;

import com.google.common.base.MoreObjects;

import java.util.Objects;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * A default implementation of a simple allele.
 */
public class Allele extends ForgeRegistryEntry<IAllele> implements IAllele {
	public static final IAllele EMPTY = new Allele("empty", false).setRegistryName(new ResourceLocation("genetics", "empty"));

	protected final boolean dominant;
	protected final String localisationKey;

	public Allele(String localisationKey, boolean dominant) {
		this.localisationKey = localisationKey;
		this.dominant = dominant;
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
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getLocalisationKey());
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
		if (!(obj instanceof IAllele)) {
			return false;
		}
		IAllele otherAllele = (IAllele) obj;
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
