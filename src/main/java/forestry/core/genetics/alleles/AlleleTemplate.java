package forestry.core.genetics.alleles;

import javax.annotation.Nullable;
import java.util.Arrays;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTemplate;
import forestry.api.genetics.IAlleleTemplateBuilder;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;

public class AlleleTemplate<T extends Enum<T> & IChromosomeType, S extends IAlleleSpecies> implements IAlleleTemplate<T, S> {

	public final IAllele[] alleles;
	protected final ISpeciesRoot root;

	AlleleTemplate(IAllele[] alleles, ISpeciesRoot root) {
		this.alleles = Arrays.copyOf(alleles, alleles.length);
		this.root = root;
	}

	@Override
	public IAllele get(T chromosomeType) {
		if(chromosomeType.getSpeciesRoot() != root){
			return null;
		}
		return alleles[chromosomeType.ordinal()];
	}

	@SuppressWarnings("unchecked")
	@Override
	public S getSpecies() {
		return (S) get((T)root.getSpeciesChromosomeType());
	}

	@Override
	public int size() {
		return alleles.length;
	}

	@Override
	public IAllele[] alleles() {
		return Arrays.copyOf(alleles, alleles.length);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AlleleTemplate<T, S> copy(){
		return new AlleleTemplate(alleles(), root);
	}

	@Override
	public IAlleleTemplateBuilder<T, S> createBuilder() {
		return new AlleleTemplateBuilder<T, S>(root, alleles);
	}

	@Override
	public ISpeciesRoot getRoot() {
		return root;
	}

	@Override
	public IChromosome[] toChromosomes(@Nullable IAlleleTemplate<T, S> inactiveTemplate) {
		if(inactiveTemplate == null) {
			return root.templateAsChromosomes(alleles);
		}
		return root.templateAsChromosomes(alleles, inactiveTemplate.alleles());
	}

	@Override
	public IGenome toGenome(@Nullable IAlleleTemplate<T, S> inactiveTemplate) {
		if(inactiveTemplate == null) {
			return root.templateAsGenome(alleles);
		}
		return root.templateAsGenome(alleles, inactiveTemplate.alleles());
	}

	@Override
	public IIndividual toIndividual(@Nullable IAlleleTemplate<T, S> inactiveTemplate) {
		if(inactiveTemplate == null) {
			return root.templateAsIndividual(alleles);
		}
		return root.templateAsIndividual(alleles, inactiveTemplate.alleles());
	}
}
