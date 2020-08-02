package forestry.api.genetics.alyzer;

import genetics.api.individual.IIndividual;

import forestry.apiculture.genetics.IOrganismTooltipProvider;

public interface IAlleleDisplayHandler<I extends IIndividual> extends IOrganismTooltipProvider<I>,
        IAlyzerDisplayProvider<I> {
}
