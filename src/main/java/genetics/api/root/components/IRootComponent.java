package genetics.api.root.components;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootBuilder;

import java.util.function.Consumer;

/**
 * Every {@link IIndividualRoot} contains {@link IRootComponent}s. They provide expand the function of the root without
 * expanding the root object itself.
 * <p>
 * A {@link IRootComponent} can be added to a root in the building stage of the root. It can be added with
 * {@link IIndividualRootBuilder#addComponent(ComponentKey, IRootComponentFactory)} or
 * {@link IIndividualRootBuilder#addComponent(ComponentKey)}. The last one of the two methods gets the
 * {@link IRootComponentFactory} that creates the builder of the component from the
 * {@link IRootComponentRegistry}. A registry where default component factories can be registered.
 * <p>
 * <p>
 * The {@link IIndividualRootBuilder} can also be used to add listeners to it with
 * {@link IIndividualRootBuilder#addListener(ComponentKey, Consumer)}. These listeners are getting called before the
 * components are getting created. You can use these listeners to register things to the component builders.
 *
 * @see IRootComponentFactory
 * @see IRootComponentRegistry
 * @see IIndividualRootBuilder
 */
public interface IRootComponent<I extends IIndividual> {
	IIndividualRoot<I> getRoot();

	ComponentKey getKey();
}
