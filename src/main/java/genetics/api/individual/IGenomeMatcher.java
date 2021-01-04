package genetics.api.individual;

import genetics.api.root.IIndividualRoot;

public interface IGenomeMatcher {
	IGenome getFirst();

	IGenome getSecond();

	IIndividualRoot getRoot();

	boolean matches();
}
