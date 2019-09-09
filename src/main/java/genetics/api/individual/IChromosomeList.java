package genetics.api.individual;

import java.util.Collection;

import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

public interface IChromosomeList extends Iterable<IChromosomeType> {
	IChromosomeTypeBuilder builder();

	Collection<IChromosomeType> types();

	int size();

	String getUID();

	IIndividualRoot getRoot();

	IRootDefinition getDefinition();

	IChromosomeType[] typesArray();
}
