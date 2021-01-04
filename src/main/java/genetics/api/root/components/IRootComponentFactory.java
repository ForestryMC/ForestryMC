package genetics.api.root.components;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootBuilder;

/**
 * A interface that can be used to create {@link IRootComponentBuilder}s and is needed by
 * {@link IIndividualRootBuilder#addComponent(ComponentKey, IRootComponentFactory)} and
 * {@link IRootComponentRegistry#registerFactory(ComponentKey, IRootComponentFactory)} to create these.
 *
 * @param <I> The type of the individual that the root object of the component builder describes.
 * @param <B> The type of the component builder this factory creates.
 * @see IRootComponentBuilder
 * @see IRootComponentFactory
 * @see IRootComponentRegistry
 * @see IIndividualRootBuilder
 */
public interface IRootComponentFactory<I extends IIndividual, C extends IRootComponent<I>> {

	C create(IIndividualRoot<I> root);
}
