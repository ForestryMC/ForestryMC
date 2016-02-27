package forestry.core.genetics;

import java.util.Map;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IClassification;

public interface IBranchDefinition<C extends IChromosomeType<C>> {
	Map<C, IAllele> getTemplate();

	IClassification getBranch();
}
