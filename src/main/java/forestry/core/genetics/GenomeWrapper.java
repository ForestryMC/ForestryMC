package forestry.core.genetics;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IGenomeWrapper;

public abstract class GenomeWrapper<T extends Enum<T> & IChromosomeType> implements IGenomeWrapper<T> {

	protected IGenome genome;

	public GenomeWrapper(IGenome genome) {
		this.genome = genome;
	}

	public  <A extends IAllele> A getActiveAllele(T chromosomeType, Class<A> alleleClass){
		IAllele allele = genome.getActiveAllele(chromosomeType);
		if(!alleleClass.isInstance(allele)){
			throw new IllegalArgumentException();
		}
		return alleleClass.cast(allele);
	}

	public  <A extends IAllele> A getInactiveAllele(T chromosomeType, Class<A> alleleClass){
		IAllele allele = genome.getInactiveAllele(chromosomeType);
		if(!alleleClass.isInstance(allele)){
			throw new IllegalArgumentException();
		}
		return alleleClass.cast(allele);
	}

	public IGenome getGenome() {
		return genome;
	}
}
