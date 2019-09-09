package forestry.sorting;

import javax.annotation.Nullable;

import genetics.api.alleles.IAllele;


public class AlleleFilter {
	@Nullable
	public IAllele activeAllele;

	@Nullable
	public IAllele inactiveAllele;

	public boolean isValid(String activeUID, String inactiveUID) {
		return (this.activeAllele == null || activeUID.equals(this.activeAllele.getRegistryName().toString()))
			&& (this.inactiveAllele == null || inactiveUID.equals(this.inactiveAllele.getRegistryName().toString()));
	}

	public boolean isEmpty() {
		return activeAllele == null && inactiveAllele == null;
	}
}
