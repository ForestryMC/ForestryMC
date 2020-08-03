package forestry.sorting;

import forestry.api.genetics.filter.IFilterData;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;

public class FilterData implements IFilterData {
    private final IRootDefinition definition;
    @Nullable
    private final IIndividual individual;
    @Nullable
    private final IOrganismType type;

    public FilterData(IRootDefinition root, @Nullable IIndividual individual, @Nullable IOrganismType type) {
        this.definition = root;
        this.individual = individual;
        this.type = type;
    }

    @Override
    public IIndividualRoot getRoot() {
        if (!definition.isPresent()) {
            throw new NoSuchElementException("No root present");
        }
        return definition.get();
    }

    @Override
    public IRootDefinition getDefinition() {
        return definition;
    }

    @Override
    public IIndividual getIndividual() {
        if (individual == null) {
            throw new NoSuchElementException("No individual present");
        }
        return individual;
    }

    @Override
    public IOrganismType getType() {
        if (type == null) {
            throw new NoSuchElementException("No type present");
        }
        return type;
    }

    @Override
    public boolean isPresent() {
        return !definition.isPresent() && individual != null && type != null;
    }
}
