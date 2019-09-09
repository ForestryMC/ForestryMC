package genetics.root;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.root.IGeneticListener;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootContext;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponent;

public class RootContext<I extends IIndividual> implements IRootContext<I> {
	private final IKaryotype karyotype;
	private final Collection<IGeneticListener<I>> listeners;
	private final Multimap<ComponentKey, Consumer> componentListeners;
	private final Function<IIndividualRoot<I>, Map<ComponentKey, IRootComponent<I>>> componentFactory;

	public RootContext(IKaryotype karyotype, Collection<IGeneticListener<I>> listeners, Multimap<ComponentKey, Consumer> componentListeners, Function<IIndividualRoot<I>, Map<ComponentKey, IRootComponent<I>>> componentFactory) {
		this.karyotype = karyotype;
		this.listeners = listeners;
		this.componentListeners = componentListeners;
		this.componentFactory = componentFactory;
	}

	public Collection<IGeneticListener<I>> getListeners() {
		return listeners;
	}

	public Multimap<ComponentKey, Consumer> getComponentListeners() {
		return componentListeners;
	}

	@Override
	public IKaryotype getKaryotype() {
		return karyotype;
	}

	@Override
	public IRootDefinition getDefinition() {
		return GeneticsAPI.apiInstance.getRoot(karyotype.getUID());
	}

	@Override
	public Map<ComponentKey, IRootComponent<I>> createComponents(IIndividualRoot<I> root) {
		return componentFactory.apply(root);
	}
}
