package genetics.api.root.components;

import javax.annotation.Nullable;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRootBuilder;

/**
 * A registry that can be used to register default factories for {@link IRootComponent}s and get these.
 *
 * @see IRootComponentFactory
 * @see IRootComponentRegistry
 * @see IIndividualRootBuilder
 */
public interface IRootComponentRegistry {
	/**
	 * Registers a default factory.
	 *
	 * @param key     The component key of the component builder that the factory creates.
	 * @param factory A factory that creates a component builder object.
	 */
	<I extends IIndividual, C extends IRootComponent<I>> void registerFactory(ComponentKey<C> key, IRootComponentFactory<I, C> factory);

	/**
	 * @return A factory if one was registered, false otherwise.
	 */
	@Nullable
	IRootComponentFactory getFactory(ComponentKey key);
}
