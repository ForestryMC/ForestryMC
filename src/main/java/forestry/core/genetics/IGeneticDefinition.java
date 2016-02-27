package forestry.core.genetics;

import com.google.common.collect.ImmutableMap;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;

public interface IGeneticDefinition<C extends IChromosomeType> {

	ImmutableMap<C, IAllele> getTemplate();

	IGenome<C> getGenome();

	IIndividual<C> getIndividual();

}
