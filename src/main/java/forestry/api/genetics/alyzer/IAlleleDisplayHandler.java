package forestry.api.genetics.alyzer;

import forestry.apiculture.genetics.IGeneticTooltipProvider;

import genetics.api.individual.IIndividual;

public interface IAlleleDisplayHandler<I extends IIndividual> extends IGeneticTooltipProvider<I>, IAlyzerDisplayProvider {
}
