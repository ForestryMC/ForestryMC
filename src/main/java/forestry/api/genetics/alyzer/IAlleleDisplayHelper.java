package forestry.api.genetics.alyzer;

import java.util.function.Predicate;

import forestry.apiculture.genetics.IGeneticTooltipProvider;

import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;

public interface IAlleleDisplayHelper {
	void addTooltip(IGeneticTooltipProvider<? extends IIndividual> provider, String rootUID, int orderingInfo);

	void addTooltip(IGeneticTooltipProvider<? extends IIndividual> provider, String rootUID, int orderingInfo, Predicate<IOrganismType> typeFilter);

	void addAlyzer(IAlyzerDisplayProvider provider, String rootUID, int orderingInfo);

}
