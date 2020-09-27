package forestry.core.genetics.alleles;

import forestry.api.genetics.alyzer.IAlleleDisplayHandler;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;

import java.util.function.Predicate;

public class AlleleDisplayManager {
    public <I extends IIndividual> void addHandler(
            IAlleleDisplayHandler<I> handler,
            String rootUID,
            IChromosomeType type,
            Predicate<IOrganismType> typeFilter
    ) {
    }

    public <I extends IIndividual> void addHandler(
            IAlleleDisplayHandler<I> handler,
            String rootUID,
            IChromosomeType type
    ) {
        addHandler(handler, rootUID, type, (t) -> true);
    }
}
