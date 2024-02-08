package forestry.core.features;

import deleteme.BiomeCategory;
import forestry.core.config.Constants;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoreFeatures {
	private static List<Holder<PlacedFeature>> OVERWORLD_ORES = List.of();

	public static void registerOres() {
		var apatite = OrePlacements.commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(48), VerticalAnchor.absolute(112)));
		var tin = OrePlacements.commonOrePlacement(16, HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.absolute(64)));

		OreConfiguration apatiteOre = new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, CoreBlocks.APATITE_ORE.defaultState(), 3);
		OreConfiguration deepslateApatiteOre = new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, CoreBlocks.DEEPSLATE_APATITE_ORE.defaultState(), 3);
		OreConfiguration tinOre = new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, CoreBlocks.TIN_ORE.defaultState(), 9);
		OreConfiguration deepslateTinOre = new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, CoreBlocks.DEEPSLATE_TIN_ORE.defaultState(), 9);

		OVERWORLD_ORES = List.of(
				registerOre("apatite_ore", apatiteOre, apatite),
				registerOre("deepslate_apatite_ore", deepslateApatiteOre, apatite),
				registerOre("tin_ore", tinOre, tin),
				registerOre("deepslate_tin_ore",deepslateTinOre, tin)
		);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void gen(BiomeLoadingEvent event) {
		if (event.getCategory() == BiomeCategory.NETHER || event.getCategory() == BiomeCategory.THEEND) {
			return;
		}

		for (Holder<PlacedFeature> feature : OVERWORLD_ORES) {
			event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, feature);
		}
	}

	private static Holder<PlacedFeature> registerOre(String registryName, OreConfiguration oreConfiguration, List<PlacementModifier> placementModifiers) {
		String identifier = new ResourceLocation(Constants.MOD_ID, registryName).toString();
		Holder<ConfiguredFeature<OreConfiguration, ?>> oreFeature = FeatureUtils.register(identifier, Feature.ORE, oreConfiguration);
		return PlacementUtils.register(identifier, oreFeature, placementModifiers);
	}
}
