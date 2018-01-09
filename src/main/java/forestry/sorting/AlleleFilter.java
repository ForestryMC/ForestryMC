package forestry.sorting;

import javax.annotation.Nullable;

import forestry.api.genetics.IAllele;

public class AlleleFilter {
	@Nullable
	public IAllele activeAllele;

	@Nullable
	public IAllele inactiveAllele;

	public boolean isValid(String activeUID, String inactiveUID) {
		return (this.activeAllele == null || activeUID.equals(this.activeAllele.getUID()))
			&& (this.inactiveAllele == null || inactiveUID.equals(this.inactiveAllele.getUID()));
	}

	public boolean isEmpty() {
		return activeAllele == null && inactiveAllele == null;
	}
}
