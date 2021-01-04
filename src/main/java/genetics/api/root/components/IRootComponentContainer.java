package genetics.api.root.components;

import genetics.api.individual.IIndividual;

import java.util.Map;
import java.util.Optional;

public interface IRootComponentContainer<I extends IIndividual> {

	void onStage(IStage stage);

	boolean has(ComponentKey key);

	<C extends IRootComponent<I>> C get(ComponentKey key);

	<C extends IRootComponent<I>> Optional<C> getSafe(ComponentKey key);

	Map<ComponentKey, IRootComponent<I>> getComponents();
}
