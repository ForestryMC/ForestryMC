/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.config;

import com.google.common.collect.LinkedListMultimap;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Property;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import forestry.Forestry;
import forestry.apiculture.HiveConfig;
import forestry.core.fluids.Fluids;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.factory.ModuleFactory;
import forestry.mail.gui.GuiMailboxInfo;

public class Config {

	public static final String CATEGORY_COMMON = "common";
	public static final String CATEGORY_FLUIDS = "fluids";
	public static final String CATEGORY_FARM = "farm";

	@Nullable
	public static LocalizedConfiguration configCommon;
	@Nullable
	public static LocalizedConfiguration configFluid;
	@Nullable
	public static String gameMode;

	private static final Set<String> disabledStructures = new HashSet<>();
	private static final Set<String> disabledFluids = new HashSet<>();
	private static final Set<String> disabledBlocks = new HashSet<>();

	public static boolean isDebug = false;

	// Graphics
	public static boolean enableParticleFX = true;

	// Humus
	public static int humusDegradeDelimiter = 3;

	// Greenhouse
	public static int climateSourceRange = 36;
	public static float climateSourceEnergyModifier = 1.5F;
	public static int greenhouseSize = 4;

	// Genetics
	public static boolean pollinateVanillaTrees = true;
	public static int analyzerEnergyPerWork = 20320;
	public static float researchMutationBoostMultiplier = 1.5f;
	public static float maxResearchMutationBoostPercent = 5.0f;

	// World generation
	public static boolean generateApatiteOre = true;
	public static boolean generateCopperOre = true;
	public static boolean generateTinOre = true;
	public static Set<Integer> blacklistedOreDims = new HashSet<>();
	public static Set<Integer> whitelistedOreDims = new HashSet<>();
	private static float generateBeehivesAmount = 1.0f;
	public static boolean generateBeehivesDebug = false;
	public static boolean logHivePlacement = false;
	public static boolean enableVillagers = true;
	public static boolean generateTrees = true;
	public static float generateTreesAmount = 1.0F;
	public static Set<Integer> blacklistedTreeDims = new HashSet<>();
	public static Set<Integer> whitelistedTreeDims = new HashSet<>();
	public static Set<BiomeDictionary.Type> blacklistedTreeTypes = new HashSet<>();
	public static Set<Biome> blacklistedTreeBiomes = new HashSet<>();

	// Retrogen
	public static boolean doRetrogen = false;
	public static boolean forceRetrogen = false;

	// Performance
	public static boolean enableBackpackResupply = true;

	// Customization
	private static boolean craftingBronzeEnabled = true;

	// Farm
	public static int farmSize = 2;
	public static float fertilizerModifier = 1.0F;
	public static boolean squareFarms = false;
	private static boolean enableExUtilEnderLily = true;
	private static boolean enableExUtilRedOrchid = true;
	private static boolean enableMagicalCropsSupport = true;

	// Cultivation
	public static int planterExtend = 4;
	public static boolean ringFarms = true;
	public static int ringSize = 4;

	// Book
	public static boolean spawnWithBook = true;

	// Mail
	public static boolean mailAlertEnabled = true;
	public static GuiMailboxInfo.XPosition mailAlertXPosition = GuiMailboxInfo.XPosition.LEFT;
	public static GuiMailboxInfo.YPosition mailAlertYPosition = GuiMailboxInfo.YPosition.TOP;

	public static boolean craftingStampsEnabled = true;
	public static final ArrayList<String> collectorStamps = new ArrayList<>();

	// Fluids
	public static boolean CapsuleFluidPickup = false;
	public static boolean nonConsumableCapsules = false;

	// Gui tabs (Ledger)
	public static int guiTabSpeed = 8;

	// Hints
	public static boolean enableHints = true;
	public static final LinkedListMultimap<String, String> hints = LinkedListMultimap.create();
	public static boolean enableEnergyStat = true;

	// Energy
	public static boolean enableRF = true;
	public static boolean enableMJ = true;
	public static boolean enableTesla = true;
	public static EnergyDisplayMode energyDisplayMode = EnergyDisplayMode.RF;

	public static boolean isStructureEnabled(String uid) {
		return !Config.disabledStructures.contains(uid);
	}

	public static boolean isFluidEnabled(Fluids fluidDefinition) {
		return !Config.disabledFluids.contains(fluidDefinition.getTag());
	}

	public static boolean isBlockEnabled(String tag) {
		return !Config.disabledBlocks.contains(tag);
	}

	public static boolean isCraftingBronzeEnabled() {
		return craftingBronzeEnabled;
	}

	public static double getBeehivesAmount() {
		return generateBeehivesAmount;
	}

	public static boolean isExUtilEnderLilyEnabled() {
		return enableExUtilEnderLily;
	}

	public static boolean isExUtilRedOrchidEnabled() {
		return enableExUtilRedOrchid;
	}

	public static boolean isMagicalCropsSupportEnabled() {
		return enableMagicalCropsSupport;
	}

	public static void blacklistTreeDim(int dimID) {
		blacklistedTreeDims.add(dimID);
	}

	public static void whitelistTreeDim(int dimID) {
		whitelistedTreeDims.add(dimID);
	}

	public static void blacklistOreDim(int dimID) {
		blacklistedOreDims.add(dimID);
	}

	public static void whitelistOreDim(int dimID) {
		whitelistedOreDims.add(dimID);
	}

	public static boolean isValidOreDim(int dimID) {        //blacklist has priority
		if (blacklistedOreDims.isEmpty() || !blacklistedOreDims.contains(dimID)) {
			return whitelistedOreDims.isEmpty() || whitelistedOreDims.contains(dimID);
		}
		return false;
	}

	public static boolean isValidTreeDim(int dimID) {        //blacklist has priority
		if (blacklistedTreeDims.isEmpty() || !blacklistedTreeDims.contains(dimID)) {
			return whitelistedTreeDims.isEmpty() || whitelistedTreeDims.contains(dimID);
		}
		return false;
	}

	public static boolean isValidTreeBiome(Biome biome) {
		if (blacklistedTreeBiomes.contains(biome)) {
			return false;
		}
		return !BiomeDictionary.getTypes(biome).stream().anyMatch(blacklistedTreeTypes::contains);
	}

	public static void load(Side side) {
		File configCommonFile = new File(Forestry.instance.getConfigFolder(), CATEGORY_COMMON + ".cfg");
		configCommon = new LocalizedConfiguration(configCommonFile, "1.2.1");
		loadConfigCommon(side);

		File configFluidsFile = new File(Forestry.instance.getConfigFolder(), CATEGORY_FLUIDS + ".cfg");
		configFluid = new LocalizedConfiguration(configFluidsFile, "1.0.0");
		loadConfigFluids();

		loadHints();
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (!event.getModID().equals(Constants.MOD_ID)) {
			return;
		}
		loadConfigCommon(FMLCommonHandler.instance().getSide());
		loadConfigFluids();
	}

	private static void loadConfigCommon(Side side) {

		gameMode = configCommon.getStringLocalized("difficulty", "game.mode", "EASY", new String[]{"OP, EASY, NORMAL, HARD"});

		boolean recreate = configCommon.getBooleanLocalized("difficulty", "recreate.definitions", true);
		if (recreate) {
			Log.info("Recreating all gamemode definitions from the defaults. This may be caused by an upgrade");

			String recreateDefinitionsComment = Translator.translateToLocal("for.config.difficulty.recreate.definitions.comment");
			Property property = configCommon.get("difficulty", "recreate.definitions", true, recreateDefinitionsComment);
			property.set(false);

			// Make sure the default mode files are there.

			File opMode = new File(Forestry.instance.getConfigFolder(), "gamemodes/OP.cfg");
			copyFileToFS(opMode, "/config/forestry/gamemodes/OP.cfg");

			File normalMode = new File(Forestry.instance.getConfigFolder(), "gamemodes/NORMAL.cfg");
			copyFileToFS(normalMode, "/config/forestry/gamemodes/NORMAL.cfg");

			File hardMode = new File(Forestry.instance.getConfigFolder(), "gamemodes/HARD.cfg");
			copyFileToFS(hardMode, "/config/forestry/gamemodes/HARD.cfg");
		}

		// RetroGen

		doRetrogen = configCommon.getBooleanLocalized("world.generate.retrogen", "normal", doRetrogen);
		forceRetrogen = configCommon.getBooleanLocalized("world.generate.retrogen", "forced", forceRetrogen);

		if (forceRetrogen) {
			Property property = configCommon.get("world.generate.retrogen", "forced", false);
			property.set(false);

			Log.info("Enabled force retrogen.");
			doRetrogen = true;
		} else if (doRetrogen) {
			Log.info("Enabled retrogen.");
		}

		generateBeehivesAmount = configCommon.getFloatLocalized("world.generate.beehives", "amount", generateBeehivesAmount, 0.0f, 10.0f);
		generateBeehivesDebug = configCommon.getBooleanLocalized("world.generate.beehives", "debug", generateBeehivesDebug);

		HiveConfig.parse(configCommon);

		generateApatiteOre = configCommon.getBooleanLocalized("world.generate.ore", "apatite", generateApatiteOre);
		generateCopperOre = configCommon.getBooleanLocalized("world.generate.ore", "copper", generateCopperOre);
		generateTinOre = configCommon.getBooleanLocalized("world.generate.ore", "tin", generateTinOre);
		for (int dimId : configCommon.get("world.generate.ore", "dimBlacklist", new int[0]).getIntList()) {
			blacklistedOreDims.add(dimId);
		}
		for (int dimId : configCommon.get("world.generate.ore", "dimWhitelist", new int[0]).getIntList()) {
			whitelistedOreDims.add(dimId);
		}

		enableVillagers = configCommon.getBooleanLocalized("world.generate", "villagers", enableVillagers);

		generateTrees = configCommon.getBooleanLocalized("world.generate", "trees", generateTrees);

		generateTreesAmount = configCommon.getFloatLocalized("world.generate.trees", "treeFrequency", generateTreesAmount, 0.0F, 10.0F);

		for (int dimId : configCommon.get("world.generate.trees", "dimBlacklist", new int[0]).getIntList()) {
			blacklistedTreeDims.add(dimId);
		}
		for (int dimId : configCommon.get("world.generate.trees", "dimWhitelist", new int[0]).getIntList()) {
			whitelistedTreeDims.add(dimId);
		}
		for (String entry : configCommon.get("world.generate.trees", "biomeblacklist", new String[0]).getStringList()) {
			BiomeDictionary.Type type = BiomeDictionary.Type.getType(entry);
			Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(entry));
			if (type != null) {
				blacklistedTreeTypes.add(type);
			} else if (biome != null) {
				blacklistedTreeBiomes.add(biome);
			}
		}


		craftingBronzeEnabled = configCommon.getBooleanLocalized("crafting", "bronze", craftingBronzeEnabled);
		craftingStampsEnabled = configCommon.getBooleanLocalized("crafting.stamps", "enabled", true);

		String[] allStamps = new String[]{"1n", "2n", "5n", "10n", "20n", "50n", "100n"};
		String[] defaultCollectors = new String[]{"20n", "50n", "100n"};
		String[] stamps = configCommon.getStringListLocalized("crafting.stamps", "disabled", defaultCollectors, allStamps);
		try {
			collectorStamps.addAll(Arrays.asList(stamps));
		} catch (RuntimeException ex) {
			Log.warning("Failed to read config for 'crafting.stamps.disabled', setting to default.");
			Property property = configCommon.get("crafting.stamps", "disabled", defaultCollectors);
			property.setToDefault();
			collectorStamps.addAll(Arrays.asList(defaultCollectors));
		}

		pollinateVanillaTrees = configCommon.getBooleanLocalized("genetics", "pollinate.vanilla.trees", pollinateVanillaTrees);
		analyzerEnergyPerWork = configCommon.getIntLocalized("genetics", "analyzerblock.energy.use", 20320, 0, 100000);
		researchMutationBoostMultiplier = configCommon.getFloatLocalized("genetics.research.boost", "multiplier", researchMutationBoostMultiplier, 1.0f, 1000.f);
		maxResearchMutationBoostPercent = configCommon.getFloatLocalized("genetics.research.boost", "max.percent", maxResearchMutationBoostPercent, 0.0f, 100.0f);

		enableBackpackResupply = configCommon.getBooleanLocalized("performance", "backpacks.resupply", enableBackpackResupply);

		humusDegradeDelimiter = configCommon.getIntLocalized("tweaks.humus", "degradeDelimiter", humusDegradeDelimiter, 1, 10);

		if (side == Side.CLIENT) {
			mailAlertEnabled = configCommon.getBooleanLocalized("tweaks.gui.mail.alert", "enabled", mailAlertEnabled);
			mailAlertXPosition = configCommon.getEnumLocalized("tweaks.gui.mail.alert", "xPosition", mailAlertXPosition, GuiMailboxInfo.XPosition.values());
			mailAlertYPosition = configCommon.getEnumLocalized("tweaks.gui.mail.alert", "yPosition", mailAlertYPosition, GuiMailboxInfo.YPosition.values());

			guiTabSpeed = configCommon.getIntLocalized("tweaks.gui.tabs", "speed", guiTabSpeed, 1, 50);
			enableHints = configCommon.getBooleanLocalized("tweaks.gui.tabs", "hints", enableHints);
			enableEnergyStat = configCommon.getBooleanLocalized("tweaks.gui.tabs", "energy", enableEnergyStat);

			enableParticleFX = configCommon.getBooleanLocalized("performance", "particleFX", enableParticleFX);
		}

		farmSize = configCommon.getIntLocalized("tweaks.farms", "size", farmSize, 1, 3);
		fertilizerModifier = configCommon.getFloatLocalized("tweaks.farms", "fertilizer", fertilizerModifier, 0.1F, 5.0F);
		squareFarms = configCommon.getBooleanLocalized("tweaks.farms", "square", squareFarms);
		enableExUtilEnderLily = configCommon.getBooleanLocalized("tweaks.farms", "enderlily", enableExUtilEnderLily);
		enableExUtilRedOrchid = configCommon.getBooleanLocalized("tweaks.farms", "redorchid", enableExUtilRedOrchid);
		enableMagicalCropsSupport = configCommon.getBooleanLocalized("tweaks.farms", "magicalcrops", enableMagicalCropsSupport);

		planterExtend = configCommon.getIntLocalized("tweaks.cultivation", "extend", planterExtend, 1, 15);
		ringFarms = configCommon.getBooleanLocalized("tweaks.cultivation", "ring", ringFarms);
		ringSize = configCommon.getIntLocalized("tweaks.cultivation", "ring_size", ringSize, 1, 8);

		CapsuleFluidPickup = configCommon.getBooleanLocalized("tweaks.capsule", "capsulePickup", CapsuleFluidPickup);
		nonConsumableCapsules = configCommon.getBooleanLocalized("tweaks.capsule", "capsuleReuseable", nonConsumableCapsules);

		climateSourceRange = configCommon.getIntLocalized("tweaks.greenhouse", "range", climateSourceRange, 9, 270);
		climateSourceEnergyModifier = configCommon.getFloatLocalized("tweaks.greenhouse", "energy", climateSourceEnergyModifier, 0.0F, 15.0F);
		greenhouseSize = configCommon.getIntLocalized("tweaks.greenhouse", "size", greenhouseSize, 1, 5);

		String[] availableStructures = new String[]{"alveary3x3", "farm3x3", "farm3x4", "farm3x5", "farm4x4", "farm5x5"};
		String[] disabledStructureArray = disabledStructures.toArray(new String[disabledStructures.size()]);
		disabledStructureArray = configCommon.getStringListLocalized("structures", "disabled", disabledStructureArray, availableStructures);

		disabledStructures.addAll(Arrays.asList(disabledStructureArray));
		for (String str : disabledStructures) {
			Log.debug("Disabled structure '{}'.", str);
		}

		isDebug = configCommon.getBooleanLocalized("debug", "enabled", isDebug);

		spawnWithBook = configCommon.getBooleanLocalized("tweaks.book", "spawn", spawnWithBook);

		enableRF = configCommon.getBooleanLocalized("power.types", "rf", true);
		enableMJ = configCommon.getBooleanLocalized("power.types", "mj", true);
		enableTesla = configCommon.getBooleanLocalized("power.types", "tesla", true);

		energyDisplayMode = configCommon.getEnumLocalized("power.display", "mode", EnergyDisplayMode.RF, EnergyDisplayMode.values());

		ModuleFactory.loadMachineConfig(configCommon);

		configCommon.save();
	}

	private static void loadConfigFluids() {
		for (Fluids fluid : Fluids.values()) {
			String fluidName = Translator.translateToLocal("fluid." + fluid.getTag());

			boolean enabledFluid = !Config.disabledFluids.contains(fluid.getTag());
			String enableFluidComment = Translator.translateToLocalFormatted("for.config.fluids.enable.format", fluidName);
			enabledFluid = configFluid.getBoolean("enableFluid", fluid.getTag(), enabledFluid, enableFluidComment);
			if (!enabledFluid) {
				Config.disabledFluids.add(fluid.getTag());
			}

			boolean enabledFluidBlock = !Config.disabledBlocks.contains(fluid.getTag());
			String enableFluidBlockComment = Translator.translateToLocalFormatted("for.config.fluid.blocks.enable.format", fluidName);
			enabledFluidBlock = configFluid.getBoolean("enableFluidBlock", fluid.getTag(), enabledFluidBlock, enableFluidBlockComment);
			if (!enabledFluidBlock) {
				Config.disabledBlocks.add(fluid.getTag());
			}
		}

		configFluid.save();
	}

	private static void copyFileToFS(File destination, String resourcePath) {
		InputStream stream = Config.class.getResourceAsStream(resourcePath);
		OutputStream outstream;
		int readBytes;
		byte[] buffer = new byte[4096];
		try {

			if (destination.getParentFile() != null) {
				destination.getParentFile().mkdirs();
			}

			if (!destination.exists() && !destination.createNewFile()) {
				return;
			}

			outstream = new FileOutputStream(destination);
			while ((readBytes = stream.read(buffer)) > 0) {
				outstream.write(buffer, 0, readBytes);
			}
		} catch (FileNotFoundException e) {
			Log.error("File not found.", e);
		} catch (IOException e) {
			Log.error("Failed to copy file.", e);
		}
	}

	private static void loadHints() {

		Properties prop = new Properties();

		try {
			InputStream hintStream = Config.class.getResourceAsStream("/config/forestry/hints.properties");
			prop.load(hintStream);
		} catch (IOException | NullPointerException e) {
			Log.error("Failed to load hints file.", e);
		}

		for (String key : prop.stringPropertyNames()) {
			String[] parsedHints = parseHints(prop.getProperty(key));
			for (String parsedHint : parsedHints) {
				hints.put(key, parsedHint);
			}
		}
	}

	private static String[] parseHints(String list) {
		if (list.isEmpty()) {
			return new String[0];
		} else {
			return list.split("[;]+");
		}
	}
}
