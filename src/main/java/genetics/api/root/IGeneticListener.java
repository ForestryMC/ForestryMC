package genetics.api.root;

import genetics.api.alleles.IAlleleRegistry;
import genetics.api.individual.IIndividual;
import genetics.api.root.components.IRootComponent;
import genetics.api.root.components.IStage;

public interface IGeneticListener<I extends IIndividual> {
	default void registerAlleles(IAlleleRegistry registry) {
	}

	default <C extends IRootComponent<I>> void onComponentCreation(C component) {

	}

	/**
	 * This method is called for every {@link IRootComponent} that was registered for the
	 * {@link IIndividualRootBuilder} of the root that the species belongs to.
	 * <p>
	 * This method gets called for every definition that was added to the {@link GatherDefinitions}
	 * event.
	 * <p>
	 * As an alternative for this method you can use the {@link CreateComponent} event.
	 * <p>
	 * It can be used to register mutations, templates, translators, etc.
	 *
	 * @param <C>       The type of the given builder.
	 * @param component The builder that is associated to the given key.
	 */
	default <C extends IRootComponent<I>> void onComponentSetup(C component) {

	}

	default void onStage(IStage stage) {

	}

	default void onRootCreation(IIndividualRoot<I> root) {

	}

	default void onBuilderCreation(IIndividualRootBuilder<I> builder) {
	}
}
