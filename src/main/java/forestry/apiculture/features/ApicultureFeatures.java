package forestry.apiculture.features;

import deleteme.Todos;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.core.config.Constants;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ApicultureFeatures {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registry.FEATURE_REGISTRY, Constants.MOD_ID);
	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, Constants.MOD_ID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Constants.MOD_ID);

	public static final RegistryObject<HiveDecorator> HIVE_DECORATOR = FEATURES.register("hive", HiveDecorator::new);
	public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_HIVE_DECORATOR = CONFIGURED_FEATURES.register("hive", () -> new ConfiguredFeature<>(HIVE_DECORATOR.get(), FeatureConfiguration.NONE));
	public static final RegistryObject<?> PLACED_HIVE_DECORATOR = PLACED_FEATURES.register("hive", () -> new PlacedFeature(CONFIGURED_HIVE_DECORATOR.getHolder().get(), List.of(
	)));

	static {
		// todo: need to rewrite this, and register the biome modifier json
		Todos.todo();
	}
}
