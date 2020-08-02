package forestry.api.genetics.alyzer;

import java.util.function.Predicate;

import genetics.api.organism.IOrganismType;

import forestry.apiculture.genetics.IOrganismTooltipProvider;

public interface IAlleleDisplayHelper {
    default void addTooltip(IOrganismTooltipProvider<?> provider, String rootUID, int info) {
        addTooltip(provider, rootUID, info, (organismType) -> true);
    }

    void addTooltip(IOrganismTooltipProvider<?> provider, String rootUID, int orderingInfo, Predicate<IOrganismType> typeFilter);
}
