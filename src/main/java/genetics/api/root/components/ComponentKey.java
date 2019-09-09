package genetics.api.root.components;

import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootBuilder;

/**
 * A key that is associated with a specific root component and its builder.
 * <p>
 * It can be used to add a component with {@link IIndividualRootBuilder#addComponent(ComponentKey)} or
 * {@link IIndividualRootBuilder#addComponent(ComponentKey, IRootComponentFactory)}.
 * <p>
 * Also it can be used to get a component with {@link IIndividualRoot#getComponentSafe(ComponentKey)}.
 *
 * @param <C> The type of the component.
 */
public class ComponentKey<C extends IRootComponent> {

	public static <C extends IRootComponent> ComponentKey<C> create(String name, Class<C> componentClass) {
		return create(name, componentClass, DefaultStage.CREATION);
	}

	public static <C extends IRootComponent> ComponentKey<C> create(String name, Class<C> componentClass, IStage stage) {
		return new ComponentKey<>(name, componentClass, stage);
	}

	private final String name;
	private final Class<C> componentClass;
	private final IStage stage;

	private ComponentKey(String name, Class<C> componentClass, IStage stage) {
		this.name = name;
		this.componentClass = componentClass;
		this.stage = stage;
	}

	@SuppressWarnings("unchecked")
	public <R> R cast(C instance) {
		return (R) instance;
	}

	public Class<C> getComponentClass() {
		return componentClass;
	}

	public String getName() {
		return name;
	}

	public IStage getStage() {
		return stage;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || (o != null && o.toString().equals(name));
	}
}
