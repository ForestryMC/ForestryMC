package forestry.modules.features;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.RegisterEvent;

/**
 * A feature can be used to provide an game object like a block, an item or an fluid. There are different implementations
 * of this class for every game objects.
 * <p>
 * Features are automatically loaded by the modules if you annotate the class that contains the public static final fields.
 * Events like {@link #register(RegisterEvent)} are automatically fired by modules.
 *
 * @see IBlockFeature
 * @see IItemFeature
 * @see IFluidFeature
 * @see FeatureType
 */
public interface IModFeature {
	String getIdentifier();

	FeatureType getType();

	String getModId();

	String getModuleId();

	void create();

	default void register(RegisterEvent event) {
	}

	@OnlyIn(Dist.CLIENT)
	default void clientSetupRenderers(EntityRenderersEvent.RegisterRenderers event) {
	}
}
