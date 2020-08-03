package forestry.api.genetics.alyzer;

import forestry.apiculture.genetics.IOrganismTooltipProvider;
import genetics.api.individual.IIndividual;

public interface IAlleleDisplayHandler<I extends IIndividual> extends IOrganismTooltipProvider<I>,
        IAlyzerDisplayProvider<I> {
}
