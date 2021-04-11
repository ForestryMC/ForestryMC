package forestry.apiculture.features;

import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

import forestry.apiculture.worldgen.HiveDecorator;

public class ApicultureFeatures {
	public static final Feature<NoFeatureConfig> HIVE_DECORATOR = new HiveDecorator();

	public static final ConfiguredFeature<?, ?> HIVE_DECORATOR_CONF = HIVE_DECORATOR.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE));

	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		IForgeRegistry<Feature<?>> registry = event.getRegistry();

		registry.register(HIVE_DECORATOR.setRegistryName("hive_decorator"));

		//Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Constants.MOD_ID, "hive_decorator"), HIVE_DECORATOR_CONF);
	}

	public static void onBiomeLoad(BiomeLoadingEvent event) {
		event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, HIVE_DECORATOR_CONF);
	}
}
