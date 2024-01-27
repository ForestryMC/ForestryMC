package forestry.apiculture.features;

import forestry.core.config.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import forestry.apiculture.worldgen.HiveDecorator;
import net.minecraftforge.registries.RegisterEvent;

public class ApicultureFeatures {
	public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "hive_decorator");
	public static final Feature<NoneFeatureConfiguration> HIVE_DECORATOR = new HiveDecorator();

	public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> HIVE_DECORATOR_CONF = FeatureUtils.register(ID.toString(), HIVE_DECORATOR);

	public static void registerFeatures(RegisterEvent event) {
		event.register(Registry.FEATURE_REGISTRY, ID, () -> HIVE_DECORATOR);
		//Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Constants.MOD_ID, "hive_decorator"), HIVE_DECORATOR_CONF);
	}

	public static void onBiomeLoad(BiomeLoadingEvent event) {
		Holder<PlacedFeature> placed = PlacementUtils.register(ID.toString(), HIVE_DECORATOR_CONF);
		event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placed);
	}
}
