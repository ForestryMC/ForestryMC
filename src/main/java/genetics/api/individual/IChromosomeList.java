package genetics.api.individual;

import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

import java.util.Collection;

public interface IChromosomeList extends Iterable<IChromosomeType> {
	IChromosomeTypeBuilder builder();

	Collection<IChromosomeType> types();

	int size();

	String getUID();

	IIndividualRoot getRoot();

	IRootDefinition getDefinition();

	IChromosomeType[] typesArray();
}
