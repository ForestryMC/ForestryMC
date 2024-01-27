package forestry.lepidopterology.features;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import forestry.core.config.Constants;
import forestry.lepidopterology.worldgen.CocoonDecorator;
import net.minecraftforge.registries.RegisterEvent;

public class LepidopterologyFeatures {
	private static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "cocoon_decorator");
	public static final Feature<NoneFeatureConfiguration> COCOON_DECORATOR = new CocoonDecorator();
	public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> COCOON_DECORATOR_CONF = FeatureUtils.register(ID.toString(), COCOON_DECORATOR);

	public static void registerFeatures(RegisterEvent event) {
		event.register(Registry.FEATURE_REGISTRY, ID, () -> COCOON_DECORATOR);
		event.register(Registry.CONFIGURED_FEATURE_REGISTRY, ID, COCOON_DECORATOR_CONF::value);
	}

	public static void onBiomeLoad(BiomeLoadingEvent event) {
		Holder<PlacedFeature> placed = PlacementUtils.register(ID.toString(), COCOON_DECORATOR_CONF);
		event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, placed);
	}
}
