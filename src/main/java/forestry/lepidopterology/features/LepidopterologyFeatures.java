package forestry.lepidopterology.features;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.config.Constants;
import forestry.lepidopterology.worldgen.CocoonDecorator;

public class LepidopterologyFeatures {
	public static final Feature<NoneFeatureConfiguration> COCOON_DECORATOR = new CocoonDecorator();

	public static final ConfiguredFeature<?, ?> COCOON_DECORATOR_CONF = COCOON_DECORATOR.configured(NoneFeatureConfiguration.INSTANCE);

	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		IForgeRegistry<Feature<?>> registry = event.getRegistry();

		registry.register(COCOON_DECORATOR.setRegistryName("cocoon_decorator"));

		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(Constants.MOD_ID, "cocoon_decorator"), COCOON_DECORATOR_CONF);
	}

	public static void onBiomeLoad(BiomeLoadingEvent event) {
		event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, COCOON_DECORATOR_CONF.placed());
	}
}
