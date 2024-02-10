package forestry.arboriculture.features;

import deleteme.Todos;
import forestry.arboriculture.worldgen.TreeDecorator;
import forestry.core.config.Constants;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ArboricultureFeatures {

	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registry.FEATURE_REGISTRY, Constants.MOD_ID);
	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, Constants.MOD_ID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Constants.MOD_ID);

	public static final RegistryObject<TreeDecorator> TREE_DECORATOR = FEATURES.register("tree", TreeDecorator::new);
	public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_TREE_DECORATOR = CONFIGURED_FEATURES.register("tree", () -> new ConfiguredFeature<>(TREE_DECORATOR.get(), FeatureConfiguration.NONE));
	public static final RegistryObject<?> PLACED_TREE_DECORATOR = PLACED_FEATURES.register("tree", () -> new PlacedFeature(CONFIGURED_TREE_DECORATOR.getHolder().get(), List.of(
	)));

	static {
		// todo: need to rewrite this, and register the biome modifier json
		Todos.todo();
	}
}
