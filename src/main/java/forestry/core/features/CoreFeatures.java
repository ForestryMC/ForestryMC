package forestry.core.features;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;

import forestry.core.config.Constants;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoreFeatures {
	private static final List<PlacedFeature> OVERWORLD_ORES = new ArrayList<>();

	public static void registerOres() {
		var apatite = OrePlacements.commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(48), VerticalAnchor.absolute(112)));
		var tin = OrePlacements.commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.absolute(64)));

		OVERWORLD_ORES.add(register("apatite_ore", Feature.ORE.configured(new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, CoreBlocks.APATITE_ORE.defaultState(), 3)), apatite));
		OVERWORLD_ORES.add(register("deepslate_apatite_ore", Feature.ORE.configured(new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, CoreBlocks.DEEPSLATE_APATITE_ORE.defaultState(), 3)), apatite));

		OVERWORLD_ORES.add(register("tin_ore", Feature.ORE.configured(new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, CoreBlocks.TIN_ORE.defaultState(), 9)), tin));
		OVERWORLD_ORES.add(register("deepslate_tin_ore", Feature.ORE.configured(new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, CoreBlocks.DEEPSLATE_TIN_ORE.defaultState(), 9)), tin));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void gen(BiomeLoadingEvent event) {
		if (event.getCategory() == Biome.BiomeCategory.NETHER || event.getCategory() == Biome.BiomeCategory.THEEND) {
			return;
		}

		for (PlacedFeature feature : OVERWORLD_ORES) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, feature);
		}
	}

	private static <C extends FeatureConfiguration, F extends Feature<C>> PlacedFeature register(String registryName, ConfiguredFeature<C, F> feature, List<PlacementModifier> placementModifiers) {
		ResourceLocation identifier = new ResourceLocation(Constants.MOD_ID, registryName);
		PlacedFeature placed = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, identifier, feature).placed(placementModifiers);
		return Registry.register(BuiltinRegistries.PLACED_FEATURE, identifier, placed);
	}
}
