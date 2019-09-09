package genetics.api.individual;

import genetics.api.alleles.IAllele;

public interface IChromosomeAllele<A extends IAllele> extends IChromosomeType {
	Class<? extends A> getAlleleClass();
}
