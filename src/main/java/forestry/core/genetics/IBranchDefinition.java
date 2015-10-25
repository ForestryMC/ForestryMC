package forestry.core.genetics;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;

public interface IBranchDefinition {
	IAllele[] getTemplate();

	IClassification getBranch();
}
