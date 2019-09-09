package genetics.api.root;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import genetics.api.IGeneticApiInstance;
import genetics.api.IGeneticPlugin;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponent;
import genetics.api.root.components.IRootComponentFactory;
import genetics.api.root.components.IRootComponentRegistry;

/**
 * The IIndividualRootBuilder offers several functions to register templates, types or something similar that can be
 * later retrieved from the {@link IIndividualRoot}.
 * <p>
 * After every {@link IGeneticPlugin} received {@link IGeneticPlugin#initRoots(IRootManager)} all
 * {@link IIndividualRootBuilder}s will be build automatically to {@link IIndividualRoot}s. You can get the instance
 * of you root from {@link IGeneticApiInstance#getRoot(String)} after it was created or you can use {@link #getDefinition()}.
 * <p>
 * You can create a instance of this with {@link IRootManager#getRoot(String)}.
 *
 * @param <I> The type of the individual that the root describes.
 */
public interface IIndividualRootBuilder<I extends IIndividual> {
	/**
	 * Adds a chromosome type to the prototype of this individual.
	 */
	IIndividualRootBuilder<I> addChromosome(IChromosomeType type);

	/**
	 * Adds the given chromosome types to the prototype of this individual.
	 */
	IIndividualRootBuilder<I> addChromosome(IChromosomeType... types);

	/**
	 * Sets the species type of the prototype of this individual.
	 */
	IIndividualRootBuilder<I> setSpeciesType(IChromosomeType speciesType);

	/**
	 * Sets the function that is used to create a template builder.
	 */
	IIndividualRootBuilder<I> setTemplateFactory(BiFunction<IKaryotype, IAllele[], IAlleleTemplateBuilder> templateFactory);

	IIndividualRootBuilder<I> setRootFactory(IIndividualRootFactory<I, IIndividualRoot<I>> rootFactory);

	IIndividualRootBuilder<I> setRootFactory(Class<? extends I> individualClass);

	IIndividualRootBuilder<I> setDefaultTemplate(Function<IAlleleTemplateBuilder, IAlleleTemplate> defaultTemplate);

	/**
	 * Returns an optional that contains the created root object.
	 * <p>
	 * Returns an empty optional if the root was not built yet.
	 *
	 * @return An optional that contains the root object of this builder if it was already built, otherwise an empty
	 * optional.
	 */
	<R extends IIndividualRoot<I>> IRootDefinition<R> getDefinition();

	/**
	 * Adds the default component factory that was registered with {@link IRootComponentRegistry#registerFactory(ComponentKey, IRootComponentFactory)}
	 * to this root.
	 * <p>
	 * {@link IRootComponentFactory#create(IIndividualRoot)} gets called later after all components were added and the
	 * builder starts to build the root.
	 * root object.
	 *
	 * @param key The key associated with the component and the builder of this component.
	 */
	IIndividualRootBuilder<I> addComponent(ComponentKey key);


	/**
	 * Adds the given component factory to this root.
	 * <p>
	 *
	 * @param key     The key associated with the component and the builder of this component.
	 * @param factory The factory that creates the instance of the component builder.
	 * @param <C>     The type of the component of the key.
	 * @param <B>     the type of the component builder that the is associated with the key and created by the factory.
	 */
	<C extends IRootComponent<I>> IIndividualRootBuilder<I> addComponent(ComponentKey key, IRootComponentFactory<I, C> factory);

	/**
	 * Adds a component listener.
	 * <p>
	 *
	 * @param key      The key associated with the component and the builder of this component.
	 * @param consumer A consumer that receives the instance of the component builder before the component gets created.
	 * @param <C>      The type of the component of the key.
	 * @param <B>      the type of the component builder that the is associated with the key and created by the factory.
	 */
	<C extends IRootComponent<I>> IIndividualRootBuilder<I> addListener(ComponentKey key, Consumer<C> consumer);
}
