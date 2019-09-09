package forestry.modules.features;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * A feature can be used to provide an game object like a block, an item or an fluid. There are different implementations
 * of this class for every game objects.
 * <p>
 * Features are automatically loaded by the modules if you annotate the class that contains the public static final fields.
 * Events like {@link #register(RegistryEvent.Register)} and {@link #create()} are automatically fired by modules.
 *
 * @see IBlockFeature
 * @see IItemFeature
 * @see binnie.core.liquid.IFluidDefinition
 * @see IMachineFeature
 * @see FeatureType
 */
public interface IModFeature<O> {
	String getIdentifier();

	IFeatureConstructor<O> getConstructor();

	FeatureType getType();

	String getModId();

	String getModuleId();

	default void init() {
	}

	default <T extends IForgeRegistryEntry<T>> void register(RegistryEvent.Register<T> event) {
	}

	default boolean isEnabled() {
		return ModFeatureRegistry.get(getModId()).isEnabled(this);
	}
}
