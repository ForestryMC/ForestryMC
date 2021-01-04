package genetics.api.mutation;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponent;

import java.util.Collection;
import java.util.List;

/**
 * This component contains all mutations of the {@link genetics.api.root.IIndividualRootBuilder}.
 * <p>
 * This is an optional component.
 *
 * @param <M> The type of the mutation.
 */
public interface IMutationContainer<I extends IIndividual, M extends IMutation> extends IRootComponent<I> {

	/**
	 * Registers the given mutation to the component.
	 */
	boolean registerMutation(M mutation);

	/**
	 * @return All registered mutations.
	 */
	List<? extends M> getMutations(boolean shuffle);

	/**
	 * @param other Allele to match mutations against.
	 * @return All registered mutations the given allele is part of.
	 */
	List<? extends M> getCombinations(IAllele other);

	/**
	 * @param other Allele to match mutations against.
	 * @return All registered mutations the give allele is resolute of.
	 */
	List<? extends M> getResultantMutations(IAllele other);

	/**
	 * @return all possible mutations that result from breeding two species
	 */
	List<? extends M> getCombinations(IAlleleSpecies parentFirst, IAlleleSpecies parentSecond, boolean shuffle);

	Collection<? extends M> getPaths(IAllele result, IChromosomeType geneType);

	@Override
	ComponentKey<IMutationContainer> getKey();
}
