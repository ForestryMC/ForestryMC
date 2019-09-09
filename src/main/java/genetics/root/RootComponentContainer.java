package genetics.root;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import genetics.api.individual.IIndividual;
import genetics.api.root.IGeneticListener;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.DefaultStage;
import genetics.api.root.components.IRootComponent;
import genetics.api.root.components.IRootComponentContainer;
import genetics.api.root.components.IStage;

public class RootComponentContainer<I extends IIndividual> implements IRootComponentContainer<I> {
	private final Map<ComponentKey, IRootComponent<I>> components;
	private final Multimap<ComponentKey, Consumer> componentListeners;
	private final Collection<IGeneticListener<I>> listeners;

	public RootComponentContainer(Map<ComponentKey, IRootComponent<I>> components, Multimap<ComponentKey, Consumer> componentListeners, Collection<IGeneticListener<I>> listeners) {
		this.components = new LinkedHashMap<>(components);
		this.listeners = listeners;
		this.componentListeners = componentListeners;
		onStage(DefaultStage.CREATION);
	}

	@Override
	public void onStage(IStage stage) {
		components.entrySet().stream()
			.filter(entry -> entry.getKey().getStage() == stage)
			.forEach(entry -> {
				listeners.forEach(listener -> listener.onComponentSetup(entry.getValue()));
				componentListeners.forEach((componentKey, consumer) -> {
					if (componentKey != entry.getKey()) {
						return;
					}
					consumer.accept(entry.getValue());
				});
			});
		listeners.forEach(listener -> listener.onStage(stage));
	}

	@Override
	public boolean has(ComponentKey key) {
		return components.containsKey(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends IRootComponent<I>> C get(ComponentKey key) {
		return (C) components.get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends IRootComponent<I>> Optional<C> getSafe(ComponentKey key) {
		return Optional.ofNullable((C) components.get(key));
	}

	@Override
	public Map<ComponentKey, IRootComponent<I>> getComponents() {
		return components;
	}
}
