package genetics.alleles;

import genetics.api.alleles.AlleleInfo;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleType;

public class AlleleSpecies extends Allele {
	protected AlleleSpecies(boolean dominant, String localisationKey) {
		super(dominant, localisationKey);
	}

	public static class Type implements IAlleleType {
		public static final Type INSTANCE = new Type();

		@Override
		public IAllele deserialize(AlleleInfo info) {
			return new AlleleSpecies(info.dominant, info.name);
		}
	}
}
