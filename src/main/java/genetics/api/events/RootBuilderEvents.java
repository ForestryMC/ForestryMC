package genetics.api.events;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IGenericEvent;

import genetics.api.individual.IIndividual;
import genetics.api.individual.ISpeciesDefinition;
import genetics.api.root.IGeneticListener;
import genetics.api.root.IIndividualRootBuilder;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponent;

/**
 * A collection of events that are fired by the {@link IIndividualRootBuilder}.
 *
 * @param <I> The type of the individual that the root builder represents.
 */
public class RootBuilderEvents<I extends IIndividual> extends Event {
	private final IIndividualRootBuilder<I> root;
	private final IRootDefinition definition;

	private RootBuilderEvents(IIndividualRootBuilder<I> root) {
		this.root = root;
		this.definition = root.getDefinition();
	}

	/**
	 * Checks if the given definition is the same like the definition of this event.
	 *
	 * @param rootDefinition The definition that should be checked.
	 * @return Checks if the given definition is the same like the definition of this event.
	 */
	public boolean isRoot(IRootDefinition rootDefinition) {
		return definition == rootDefinition;
	}

	/**
	 * The root builder that fired this event.
	 *
	 * @return The root builder that fired this event.
	 */
	public IIndividualRootBuilder<I> getRoot() {
		return root;
	}

	/**
	 * This event gets fired before the build phase of the {@link IIndividualRootBuilder}. For every {@link ISpeciesDefinition}
	 * that gets added with {@link #add(IGeneticListener)} the {@link ISpeciesDefinition#onComponentSetup(IRootComponent)}
	 * method gets called later for every {@link IRootComponent} that was added to the {@link IIndividualRootBuilder}.
	 *
	 * @param <I> The type of the individual that the root builder represents.
	 */
	public static class GatherListeners<I extends IIndividual> extends Event {
		private final List<IGeneticListener<I>> listeners = new LinkedList<>();
		private final IRootDefinition definition;
		private final String uid;

		public GatherListeners(IRootDefinition definition, String uid) {
			this.definition = definition;
			this.uid = uid;
		}

		public IRootDefinition getDefinition() {
			return definition;
		}

		public String getUID() {
			return uid;
		}

		public void add(IGeneticListener<I> listener) {
			this.listeners.add(listener);
		}

		@SafeVarargs
		public final void add(IGeneticListener<I>... listeners) {
			add(Arrays.asList(listeners));
		}

		public void add(Collection<IGeneticListener<I>> listeners) {
			this.listeners.addAll(listeners);
		}

		public List<IGeneticListener<I>> getListeners() {
			return Collections.unmodifiableList(listeners);
		}
	}

	/**
	 * This event gets fired after the creation of an {@link IRootComponent}.
	 *
	 * @param <I> The type of the individual that the root builder represents.
	 * @param <C> The type of the component.
	 */
	public static class CreateComponent<I extends IIndividual, C extends IRootComponent> extends RootBuilderEvents<I> implements IGenericEvent<C> {

		private final ComponentKey<?> key;
		private final C component;

		public CreateComponent(IIndividualRootBuilder<I> root, ComponentKey<?> key, C component) {
			super(root);
			this.key = key;
			this.component = component;
		}

		/**
		 * The component builder this event was for fired for.
		 *
		 * @return The component builder this was event for fired for.
		 */
		public C getComponent() {
			return component;
		}

		/**
		 * The component key of the component builder.
		 *
		 * @return The component key of the component builder.
		 */
		public ComponentKey getKey() {
			return key;
		}

		@Override
		public Type getGenericType() {
			return key.getComponentClass();
		}
	}
}
