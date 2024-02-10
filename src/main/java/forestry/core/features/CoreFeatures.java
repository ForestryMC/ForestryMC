package forestry.core.features;

import forestry.core.config.Constants;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class CoreFeatures {

	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, Constants.MOD_ID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Constants.MOD_ID);

	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_APATITE = CONFIGURED_FEATURES.register("ore_apatite",
			() -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
					OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, CoreBlocks.APATITE_ORE.defaultState()),
					OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, CoreBlocks.DEEPSLATE_APATITE_ORE.defaultState())
			), 9)));

	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_TIN = CONFIGURED_FEATURES.register("ore_tin",
			() -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(List.of(
					OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, CoreBlocks.TIN_ORE.defaultState()),
					OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, CoreBlocks.DEEPSLATE_TIN_ORE.defaultState())
			), 9)));

	public static final RegistryObject<PlacedFeature> PLACED_APATITE = PLACED_FEATURES.register("ore_apatite",
			() -> new PlacedFeature(ORE_APATITE.getHolder().get(), OrePlacements.commonOrePlacement(3, HeightRangePlacement.triangle(
					VerticalAnchor.absolute(48),
					VerticalAnchor.absolute(112)
			))));

	public static final RegistryObject<PlacedFeature> PLACED_TIN = PLACED_FEATURES.register("ore_tin",
			() -> new PlacedFeature(ORE_TIN.getHolder().get(), OrePlacements.commonOrePlacement(16, HeightRangePlacement.triangle(
					VerticalAnchor.bottom(), VerticalAnchor.absolute(64)
			))));
}
