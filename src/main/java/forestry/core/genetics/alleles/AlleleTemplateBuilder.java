package forestry.core.genetics.alleles;

import java.util.Arrays;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTemplate;
import forestry.api.genetics.IAlleleTemplateBuilder;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.IGeneticDefinition;

public class AlleleTemplateBuilder<T extends Enum<T> & IChromosomeType, S extends IAlleleSpecies> implements IAlleleTemplateBuilder<T, S> {

	public final IAllele[] alleles;
	public final ISpeciesRoot root;

	public AlleleTemplateBuilder(IGeneticDefinition definition) {
		this(definition.getGenome().getSpeciesRoot(), definition.getTemplate());
	}

	public AlleleTemplateBuilder(ISpeciesRoot root) {
		this(root, root.getDefaultTemplate());
	}

	public AlleleTemplateBuilder(ISpeciesRoot root, IAllele[] alleles) {
		this.alleles = Arrays.copyOf(alleles, alleles.length);
		this.root = root;
	}

	@Override
	public AlleleTemplateBuilder<T, S> setSpecies(S species) {
		AlleleHelper.getInstance().set(alleles, (T) root.getSpeciesChromosomeType(), species);
		return this;
	}

	@Override
	public AlleleTemplateBuilder<T, S> set(T chromosomeType, IAllele allele){
		AlleleHelper.getInstance().set(alleles, chromosomeType, allele);
		return this;
	}

	public AlleleTemplateBuilder<T, S> set(T chromosomeType, IAlleleValue value) {
		AlleleHelper.getInstance().set(alleles, chromosomeType, value);
		return this;
	}

	@Override
	public AlleleTemplateBuilder<T, S> set(T chromosomeType, boolean value) {
		AlleleHelper.getInstance().set(alleles, chromosomeType, value);
		return this;
	}

	@Override
	public AlleleTemplateBuilder<T, S> set(T chromosomeType, int value) {
		AlleleHelper.getInstance().set(alleles, chromosomeType, value);
		return this;
	}

	@Override
	public AlleleTemplateBuilder<T, S> set(T chromosomeType, float value) {
		AlleleHelper.getInstance().set(alleles, chromosomeType, value);
		return this;
	}

	@Override
	public ISpeciesRoot getRoot() {
		return root;
	}

	@Override
	public IAlleleTemplate<T, S> build() {
		return new AlleleTemplate(alleles, root);
	}

	@Override
	public int size() {
		return alleles.length;
	}
}
