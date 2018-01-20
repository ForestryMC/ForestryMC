package forestry.sorting;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;

import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;

public class FilterData implements IFilterData {
	@Nullable
	private ISpeciesRoot root;
	@Nullable
	private IIndividual individual;
	@Nullable
	private ISpeciesType type;

	public FilterData(@Nullable ISpeciesRoot root, @Nullable IIndividual individual, @Nullable ISpeciesType type) {
		this.root = root;
		this.individual = individual;
		this.type = type;
	}

	@Override
	public ISpeciesRoot getRoot() {
		if (root == null) {
			throw new NoSuchElementException("No root present");
		}
		return root;
	}

	@Override
	public IIndividual getIndividual() {
		if (individual == null) {
			throw new NoSuchElementException("No individual present");
		}
		return individual;
	}

	@Override
	public ISpeciesType getType() {
		if (type == null) {
			throw new NoSuchElementException("No type present");
		}
		return type;
	}

	@Override
	public boolean isPresent() {
		return root != null && individual != null && type != null;
	}
}
