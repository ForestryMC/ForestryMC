package forestry.api.genetics.alyzer;

import forestry.apiculture.genetics.IOrganismTooltipProvider;
import genetics.api.organism.IOrganismType;

import java.util.function.Predicate;

public interface IAlleleDisplayHelper {
    default void addTooltip(IOrganismTooltipProvider<?> provider, String rootUID, int info) {
        addTooltip(provider, rootUID, info, (organismType) -> true);
    }

    void addTooltip(
            IOrganismTooltipProvider<?> provider,
            String rootUID,
            int orderingInfo,
            Predicate<IOrganismType> typeFilter
    );
}
