package genetics.api.root;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponent;

public interface IRootContext<I extends IIndividual> {

	IKaryotype getKaryotype();

	IRootDefinition getDefinition();

	Collection<IGeneticListener<I>> getListeners();

	Multimap<ComponentKey, Consumer> getComponentListeners();

	Map<ComponentKey, IRootComponent<I>> createComponents(IIndividualRoot<I> root);
}
