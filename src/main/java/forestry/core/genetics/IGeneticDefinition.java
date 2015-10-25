package forestry.core.genetics;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;

public interface IGeneticDefinition {

	IAllele[] getTemplate();

	IGenome getGenome();

	IIndividual getIndividual();

}
