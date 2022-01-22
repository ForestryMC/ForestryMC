package forestry.core.features;

import java.util.ArrayList;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;

import forestry.core.blocks.EnumResourceType;
import forestry.core.config.Constants;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoreFeatures {
	private static final ArrayList<ConfiguredFeature<?, ?>> overworldOres = new ArrayList<ConfiguredFeature<?, ?>>();

	public static void registerOres() {
		overworldOres.add(register("apatite_ore", Feature.ORE.configured(new OreConfiguration(OreFeatures.NATURAL_STONE, CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE).defaultState(), 36))));

		overworldOres.add(register("tin_ore", Feature.ORE.configured(new OreConfiguration(OreFeatures.NATURAL_STONE, CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).defaultState(), 6))));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void gen(BiomeLoadingEvent event) {
		if (event.getCategory() == Biome.BiomeCategory.NETHER || event.getCategory() == Biome.BiomeCategory.THEEND) {
			return;
		}

		BiomeGenerationSettingsBuilder generation = event.getGeneration();
		for (ConfiguredFeature<?, ?> ore : overworldOres) {
			if (ore != null) {
				generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ore.placed());
			}
		}
	}

	private static <FC extends FeatureConfiguration> ConfiguredFeature<FC, ?> register(String name, ConfiguredFeature<FC, ?> configuredFeature) {
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Constants.MOD_ID + ":" + name, configuredFeature);
	}
}
