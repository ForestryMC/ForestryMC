package genetics.alleles;

import com.google.common.base.MoreObjects;

import java.util.Objects;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.registries.ForgeRegistryEntry;

import genetics.api.alleles.AlleleInfo;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleType;

public class Allele extends ForgeRegistryEntry<IAllele> implements IAllele {

	protected final boolean dominant;
	private final String localisationKey;

	public Allele(boolean dominant, String localisationKey) {
		this.dominant = dominant;
		this.localisationKey = localisationKey;
	}

	@Override
	public boolean isDominant() {
		return dominant;
	}

	@Override
	public final String getLocalisationKey() {
		return localisationKey;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getLocalisationKey());
	}

	@Override
	public IAlleleType getType() {
		return Type.INSTANCE;
	}

	@Override
	public int hashCode() {
		return getRegistryName() != null ? getRegistryName().hashCode() : Objects.hash(dominant);
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
			.toString();
	}

	public static class Type implements IAlleleType {
		public static final Type INSTANCE = new Type();

		@Override
		public IAllele deserialize(AlleleInfo info) {
			return new Allele(info.dominant, info.name);
		}
	}
}
