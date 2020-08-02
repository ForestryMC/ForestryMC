package forestry.core.genetics.alleles;

import java.util.function.Predicate;

import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;

import forestry.api.genetics.alyzer.IAlleleDisplayHandler;

public class AlleleDisplayManager {
    public <I extends IIndividual> void addHandler(IAlleleDisplayHandler<I> handler, String rootUID, IChromosomeType type, Predicate<IOrganismType> typeFilter) {
    }

    public <I extends IIndividual> void addHandler(IAlleleDisplayHandler<I> handler, String rootUID, IChromosomeType type) {
        addHandler(handler, rootUID, type, (t) -> true);
    }
}
