package forestry.arboriculture.features;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

import forestry.arboriculture.worldgen.TreeDecorator;
import forestry.core.config.Constants;

public class ArboricultureFeatures {
	public static final Feature<NoneFeatureConfiguration> TREE_DECORATOR = new TreeDecorator();

	public static final ConfiguredFeature<?, ?> TREE_DECORATOR_CONF = TREE_DECORATOR.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE));

	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		IForgeRegistry<Feature<?>> registry = event.getRegistry();

		registry.register(TREE_DECORATOR.setRegistryName("tree_decorator"));

		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(Constants.MOD_ID, "tree_decorator"), TREE_DECORATOR_CONF);
	}

	public static void onBiomeLoad(BiomeLoadingEvent event) {
		event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TREE_DECORATOR_CONF);
	}
}
