package forestry.lepidopterology.features;

import deleteme.Todos;
import forestry.core.config.Constants;
import forestry.lepidopterology.worldgen.CocoonDecorator;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class LepidopterologyFeatures {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registry.FEATURE_REGISTRY, Constants.MOD_ID);
	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, Constants.MOD_ID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Constants.MOD_ID);

	public static final RegistryObject<CocoonDecorator> COCOON_DECORATOR = FEATURES.register("cocoon", CocoonDecorator::new);
	public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_COCOON_DECORATOR = CONFIGURED_FEATURES.register("cocoon", () -> new ConfiguredFeature<>(COCOON_DECORATOR.get(), FeatureConfiguration.NONE));
	public static final RegistryObject<?> PLACED_COCOON_DECORATOR = PLACED_FEATURES.register("cocoon", () -> new PlacedFeature(CONFIGURED_COCOON_DECORATOR.getHolder().get(), List.of(
	)));

	static {
		// todo: need to rewrite this, and register the biome modifier json
		Todos.todo();
	}
}
