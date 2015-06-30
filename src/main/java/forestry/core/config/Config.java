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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.minecraft.util.StatCollector;

import net.minecraftforge.common.config.Property;

import forestry.Forestry;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class Config {

	public static final String CATEGORY_COMMON = "common";
	public static final String CATEGORY_FLUIDS = "fluids";
	public static final String CATEGORY_DEBUG = "debug";

	public static forestry.core.config.deprecated.Configuration configOld;

	public static LocalizedConfiguration configCommon;
	public static LocalizedConfiguration configFluid;

	public static String gameMode;

	private static final Set<String> disabledStructures = new HashSet<String>();
	private static final Set<String> disabledFluids = new HashSet<String>();
	private static final Set<String> disabledBlocks = new HashSet<String>();

	public static boolean isDebug = false;

	public static boolean enablePermissions = true;

	// Graphics
	public static boolean enableParticleFX = true;

	// Genetics
	public static boolean clearInvalidChromosomes = false;
	public static float researchMutationBoostMultiplier = 1.5f;
	public static float maxResearchMutationBoostPercent = 5.0f;

	// Dungeon loot
	public static boolean dungeonLootRare = false;

	// World generation
	public static boolean generateApatiteOre = true;
	public static boolean generateCopperOre = true;
	public static boolean generateTinOre = true;
	private static float generateBeehivesAmount = 1.0f;
	public static boolean generateBeehivesDebug = false;
	public static boolean enableVillagers = true;

	// Retrogen
	public static boolean doRetrogen = false;
	public static boolean forceRetrogen = false;

	// Performance
	public static boolean enableBackpackResupply = true;

	// Customization
	public static boolean tooltipLiquidAmount = true;

	private static boolean craftingBronzeEnabled = true;

	public static boolean craftingStampsEnabled = true;
	public static final ArrayList<String> collectorStamps = new ArrayList<String>();

	public static int farmSize = 2;
	public static boolean squareFarms = false;
	public static boolean enableExUtilEnderLily = true;
	public static boolean enableMagicalCropsSupport = true;

	// Mail
	public static boolean mailAlertEnabled = true;

	// Gui tabs (Ledger)
	public static int guiTabSpeed = 8;

	// Hints
	public static boolean enableHints = true;
	public static final Map<String, String[]> hints = new HashMap<String, String[]>();
	public static boolean enableEnergyStat = true;

	public static boolean isStructureEnabled(String uid) {
		return !Config.disabledStructures.contains(uid);
	}

	public static boolean isFluidEnabled(Fluids fluids) {
		return !Config.disabledFluids.contains(fluids.getTag());
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

	public static boolean isMagicalCropsSupportEnabled() {
		return enableMagicalCropsSupport;
	}

	public static void load() {

		configOld = new forestry.core.config.deprecated.Configuration();

		final String oldConfigCommon = CATEGORY_COMMON + ".conf";
		final String newConfigCommon = CATEGORY_COMMON + ".cfg";
		File newConfigCommonFile = new File(Forestry.instance.getConfigFolder(), newConfigCommon);
		File oldConfigCommonFile = new File(Forestry.instance.getConfigFolder(), oldConfigCommon);
		if (oldConfigCommonFile.exists()) {
			loadOldConfigCommon();

			final String oldConfigRenamed = CATEGORY_COMMON + ".conf.old";
			File oldConfigFileRenamed = new File(Forestry.instance.getConfigFolder(), oldConfigRenamed);
			if (oldConfigCommonFile.renameTo(oldConfigFileRenamed)) {
				Proxies.log.info("Migrated " + CATEGORY_COMMON + " settings to the new file '" + newConfigCommon + "' and renamed '" + oldConfigCommon + "' to '" + oldConfigRenamed + "'.");
			}
		}
		loadNewConfigCommon(newConfigCommonFile);

		final String oldConfigFluids = CATEGORY_FLUIDS + ".conf";
		final String newConfigFluids = CATEGORY_FLUIDS + ".cfg";
		File newConfigFluidsFile = new File(Forestry.instance.getConfigFolder(), newConfigFluids);
		File oldConfigFluidsFile = new File(Forestry.instance.getConfigFolder(), oldConfigFluids);
		if (oldConfigFluidsFile.exists()) {
			loadOldConfigFluids();

			final String oldConfigRenamed = CATEGORY_FLUIDS + ".conf.old";
			File oldConfigFileRenamed = new File(Forestry.instance.getConfigFolder(), oldConfigRenamed);
			if (oldConfigFluidsFile.renameTo(oldConfigFileRenamed)) {
				Proxies.log.info("Migrated " + CATEGORY_FLUIDS + " settings to the new file '" + newConfigFluids + "' and renamed '" + oldConfigFluids + "' to '" + oldConfigRenamed + "'.");
			}
		}
		loadNewConfigFluids(newConfigFluidsFile);

		final String oldConfigDebug = CATEGORY_DEBUG + ".conf";
		File oldConfigDebugFile = new File(Forestry.instance.getConfigFolder(), oldConfigDebug);
		if (oldConfigDebugFile.exists()) {
			final String oldConfigRenamed = CATEGORY_DEBUG + ".conf.old";
			File oldConfigFileRenamed = new File(Forestry.instance.getConfigFolder(), oldConfigRenamed);
			if (oldConfigDebugFile.renameTo(oldConfigFileRenamed)) {
				Proxies.log.info("Renamed '" + oldConfigDebug + "' to '" + oldConfigRenamed + "'.");
			}
		}

		loadHints();
	}

	private static void loadNewConfigCommon(File configFileCommon) {

		configCommon = new LocalizedConfiguration(configFileCommon, "1.0.0");

		gameMode = configCommon.getStringLocalized("difficulty", "game.mode", "EASY", new String[]{"OP, EASY, NORMAL, HARD"});

		boolean recreate = configCommon.getBooleanLocalized("difficulty", "recreate.definitions", true);
		if (recreate) {
			Proxies.log.info("Recreating all gamemode definitions from the defaults. This may be caused by an upgrade");

			String recreateDefinitionsComment = StringUtil.localize("config.difficulty.recreate.definitions.comment");
			Property property = configCommon.get("difficulty", "recreate.definitions", true, recreateDefinitionsComment);
			property.set(false);

			// Make sure the default mode files are there.
			File easyMode = new File(Forestry.instance.getConfigFolder(), "gamemodes/EASY.cfg");
			CopyFileToFS(easyMode, "/config/forestry/gamemodes/EASY.cfg");

			File opMode = new File(Forestry.instance.getConfigFolder(), "gamemodes/OP.cfg");
			CopyFileToFS(opMode, "/config/forestry/gamemodes/OP.cfg");

			File normalMode = new File(Forestry.instance.getConfigFolder(), "gamemodes/NORMAL.cfg");
			CopyFileToFS(normalMode, "/config/forestry/gamemodes/NORMAL.cfg");

			File hardMode = new File(Forestry.instance.getConfigFolder(), "gamemodes/HARD.cfg");
			CopyFileToFS(hardMode, "/config/forestry/gamemodes/HARD.cfg");
		}

		enableParticleFX = configCommon.getBooleanLocalized("performance", "particleFX", enableParticleFX);

		// RetroGen

		doRetrogen = configCommon.getBooleanLocalized("world.generate.retrogen", "normal", doRetrogen);
		forceRetrogen = configCommon.getBooleanLocalized("world.generate.retrogen", "forced", forceRetrogen);

		if (forceRetrogen) {
			Property property = configCommon.get("world.generate.retrogen", "forced", false);
			property.set(false);

			Proxies.log.info("Enabled force retrogen.");
			doRetrogen = true;
		} else if (doRetrogen) {
			Proxies.log.info("Enabled retrogen.");
		}

		generateBeehivesAmount = configCommon.getFloatLocalized("world.generate.beehives", "amount", generateBeehivesAmount, 0.0f, 10.0f);
		generateBeehivesDebug = configCommon.getBooleanLocalized("world.generate.beehives", "debug", generateBeehivesDebug);

		generateApatiteOre = configCommon.getBooleanLocalized("world.generate.ore", "apatite", generateApatiteOre);
		generateCopperOre = configCommon.getBooleanLocalized("world.generate.ore", "copper", generateCopperOre);
		generateTinOre = configCommon.getBooleanLocalized("world.generate.ore", "tin", generateTinOre);

		enableVillagers = configCommon.getBooleanLocalized("world.generate", "villagers", enableVillagers);

		craftingBronzeEnabled = configCommon.getBooleanLocalized("crafting", "bronze", craftingBronzeEnabled);
		craftingStampsEnabled = configCommon.getBooleanLocalized("crafting.stamps", "enabled", true);

		String[] allStamps = new String[]{"1n", "2n", "5n", "10n", "20n", "50n", "100n"};
		String[] defaultCollectors = new String[]{"20n", "50n", "100n"};
		String[] stamps = configCommon.getStringListLocalized("crafting.stamps", "disabled", defaultCollectors, allStamps);
		try {
			collectorStamps.addAll(Arrays.asList(stamps));
		} catch (Exception ex) {
			Proxies.log.warning("Failed to read config for 'crafting.stamps.disabled', setting to default.");
			Property property = configCommon.get("crafting.stamps", "disabled", defaultCollectors);
			property.setToDefault();
			collectorStamps.addAll(Arrays.asList(defaultCollectors));
		}

		clearInvalidChromosomes = configCommon.getBooleanLocalized("genetics", "clear.invalid.chromosomes", clearInvalidChromosomes);
		researchMutationBoostMultiplier = configCommon.getFloatLocalized("genetics.research.boost", "multiplier", researchMutationBoostMultiplier, 1.0f, 1000.f);
		maxResearchMutationBoostPercent = configCommon.getFloatLocalized("genetics.research.boost", "max.percent", maxResearchMutationBoostPercent, 0.0f, 100.0f);

		dungeonLootRare = configCommon.getBooleanLocalized("difficulty", "loot.rare", dungeonLootRare);

		enableBackpackResupply = configCommon.getBooleanLocalized("performance", "backpacks.resupply", enableBackpackResupply);

		mailAlertEnabled = configCommon.getBooleanLocalized("tweaks.gui", "mail.alert", mailAlertEnabled);

		guiTabSpeed = configCommon.getIntLocalized("tweaks.gui.tabs", "speed", guiTabSpeed, 1, 50);
		enableHints = configCommon.getBooleanLocalized("tweaks.gui.tabs", "hints", enableHints);
		enableEnergyStat = configCommon.getBooleanLocalized("tweaks.gui.tabs", "energy", enableEnergyStat);

		tooltipLiquidAmount = configCommon.getBooleanLocalized("tweaks.gui.tooltip", "liquidamount", tooltipLiquidAmount);

		enablePermissions = configCommon.getBooleanLocalized("tweaks", "permissions", enablePermissions);

		farmSize = configCommon.getIntLocalized("tweaks.farms", "size", farmSize, 1, 3);
		squareFarms = configCommon.getBooleanLocalized("tweaks.farms", "square", squareFarms);
		enableExUtilEnderLily = configCommon.getBooleanLocalized("tweaks.farms", "enderlily", enableExUtilEnderLily);
		enableMagicalCropsSupport = configCommon.getBooleanLocalized("tweaks.farms", "magicalcrops", enableMagicalCropsSupport);

		String[] availableStructures = new String[]{"alveary3x3", "farm3x3", "farm3x4", "farm3x5", "farm4x4", "farm5x5"};
		String[] disabledStructureArray = disabledStructures.toArray(new String[disabledStructures.size()]);
		disabledStructureArray = configCommon.getStringListLocalized("structures", "disabled", disabledStructureArray, availableStructures);

		disabledStructures.addAll(Arrays.asList(disabledStructureArray));
		for (String str : disabledStructures) {
			Proxies.log.finer("Disabled structure '%s'.", str);
		}

		isDebug = configCommon.getBooleanLocalized("debug", "enabled", isDebug);

		configCommon.save();
	}

	private static void loadOldConfigCommon() {
		forestry.core.config.deprecated.Property property;

		property = configOld.get("difficulty.gamemode", CATEGORY_COMMON, "EASY");
		property.comment = "set to your preferred game mode. available modes are OP, EASY, NORMAL, HARD. mismatch with the server may cause visual glitches with recipes. setting an unavailable mode will create a new mode definition file.";
		gameMode = property.value;

		property = configOld.get("performance.particleFX.enabled", CATEGORY_COMMON, true);
		property.comment = "set to false to disable particle fx on slower machines";
		enableParticleFX = Boolean.parseBoolean(property.value);

		// RetroGen

		String retroGenMessage = "Forestry will attempt worldGen in chunks that were created before the mod was added.";
		property = configOld.get("world.retrogen.normal", CATEGORY_COMMON, false);
		property.comment = "Set to true, " + retroGenMessage;
		doRetrogen = Boolean.parseBoolean(property.value);

		String forcedRetroGenMessage = "Forestry will attempt worldGen in all chunks for this game instance. Config option will be set to false after this run.";
		String forcedRetroGenKey = "world.retrogen.forced";
		property = configOld.get(forcedRetroGenKey, CATEGORY_COMMON, false);
		property.comment = "Set to true, " + forcedRetroGenMessage;
		forceRetrogen = Boolean.parseBoolean(property.value);

		if (forceRetrogen) {
			Proxies.log.info(forcedRetroGenMessage);
			configOld.set(forcedRetroGenKey, CATEGORY_COMMON, false);
			doRetrogen = true;
		} else if (doRetrogen) {
			Proxies.log.info(retroGenMessage);
		}

		property = configOld.get("world.generate.apatite", CATEGORY_COMMON, true);
		property.comment = "set to false to force forestry to skip generating own apatite ore blocks in the world";
		generateApatiteOre = Boolean.parseBoolean(property.value);

		property = configOld.get("world.generate.beehives.rate", CATEGORY_COMMON, generateBeehivesAmount);
		property.comment = "set how many beehives spawn. Default is 1.0, double is 2.0, half is 0.5 etc.";
		generateBeehivesAmount = Float.parseFloat(property.value);

		property = configOld.get("world.generate.beehives", CATEGORY_COMMON, true);
		property.comment = "set to false to force forestry to skip generating beehives in the world";
		boolean generateBeehives = Boolean.parseBoolean(property.value);
		if (!generateBeehives) {
			generateBeehivesAmount = 0.0f;
		}

		property = configOld.get("world.generate.beehives.debug", CATEGORY_DEBUG, false);
		property.comment = "Set to true to force Forestry to try to generate a beehive at every possible location.";
		generateBeehivesDebug = Boolean.parseBoolean(property.value);

		property = configOld.get("world.generate.copper", CATEGORY_COMMON, true);
		property.comment = "set to false to force forestry to skip generating own copper ore blocks in the world";
		generateCopperOre = Boolean.parseBoolean(property.value);

		property = configOld.get("world.generate.tin", CATEGORY_COMMON, true);
		property.comment = "set to false to force forestry to skip generating own tin ore blocks in the world";
		generateTinOre = Boolean.parseBoolean(property.value);

		property = configOld.get("world.generate.villager", CATEGORY_COMMON, true);
		property.comment = "set to false to disable the creation of forestry villagers and their houses";
		enableVillagers = Boolean.parseBoolean(property.value);

		property = configOld.get("crafting.bronze.enabled", CATEGORY_COMMON, true);
		property.comment = "set to false to disable crafting recipe for bronze";
		craftingBronzeEnabled = Boolean.parseBoolean(property.value);

		property = configOld.get("crafting.stamps.enabled", CATEGORY_COMMON, true);
		property.comment = "set to false to disable crafting recipes for all types of stamps.";
		craftingStampsEnabled = Boolean.parseBoolean(property.value);

		String defaultCollectors = "20n;50n;100n;200n";
		property = configOld.get("crafting.stamps.collector", CATEGORY_COMMON, defaultCollectors);
		property.comment = "if crafting of stamps is generally allowed, these stamps are still excluded from crafting.";
		try {
			collectorStamps.addAll(Arrays.asList(parseStamps(property.value)));
		} catch (Exception ex) {
			configOld.set("crafting.stamps.collector", CATEGORY_COMMON, defaultCollectors);
			collectorStamps.addAll(Arrays.asList(parseStamps(defaultCollectors)));
		}

		property = configOld.get("tweaks.mailalert.enabled", CATEGORY_COMMON, true);
		property.comment = "set to false to disable the mail alert box";
		mailAlertEnabled = Boolean.parseBoolean(property.value);

		property = configOld.get("tweaks.gui.tab.speed", CATEGORY_COMMON, guiTabSpeed);
		property.comment = "set the speed at which the gui side tabs open and close.";
		guiTabSpeed = Integer.parseInt(property.value);

		property = configOld.get("genetics.clear.invalid.chromosomes", CATEGORY_COMMON, clearInvalidChromosomes);
		property.comment = "set to true to clear chromosomes which contain invalid alleles. might rescue your save if it is crashing after the removal of a bee addon.";
		clearInvalidChromosomes = Boolean.parseBoolean(property.value);

		property = configOld.get("difficulty.dungeonloot.rare", CATEGORY_COMMON, false);
		property.comment = "set to true to make dungeon loot generated by forestry rarer";
		dungeonLootRare = Boolean.parseBoolean(property.value);

		property = configOld.get("performance.backpacks.resupply", CATEGORY_COMMON, true);
		property.comment = "leaving this enabled will cycle the list of active players PER INGAME TICK to check for resupply via backpack. you want to set this to false on busy servers.";
		Config.enableBackpackResupply = Boolean.parseBoolean(property.value);

		property = configOld.get("tweaks.hints.disabled", CATEGORY_COMMON, false);
		property.comment = "set to true to disable hints on machine and engine guis.";
		Config.enableHints = !Boolean.parseBoolean(property.value);
		property = configOld.get("tweaks.energystat.disabled", CATEGORY_COMMON, true);
		property.comment = "set to true to disable energy statistics on energy consumers.";
		Config.enableEnergyStat = !Boolean.parseBoolean(property.value);
		property = configOld.get("tweaks.tooltip.liquidamount.disabled", CATEGORY_COMMON, false);
		property.comment = "set to true to disable displaying liquid amounts in tank tooltips.";
		Config.tooltipLiquidAmount = !Boolean.parseBoolean(property.value);

		property = configOld.get("tweaks.permissions.disabled", CATEGORY_COMMON, false);
		property.comment = "set to true to disable access restrictions on forestry machines.";
		Config.enablePermissions = !Boolean.parseBoolean(property.value);

		property = configOld.get("tweaks.farms.squared", CATEGORY_COMMON, false);
		property.comment = "set to true to have farms use a square layout instead of a diamond one.";
		Config.squareFarms = Boolean.parseBoolean(property.value);

		property = configOld.get("tweaks.farms.exutilenderlily", CATEGORY_COMMON, true);
		property.comment = "set to false to disable multifarm support for ExtraUtilities Ender-lily seeds.";
		enableExUtilEnderLily = Boolean.parseBoolean(property.value);

		property = configOld.get("tweaks.farms.magicalcropssupport", CATEGORY_COMMON, true);
		property.comment = "set to false to disable multifarm support for Magical Crops crops.";
		enableMagicalCropsSupport = Boolean.parseBoolean(property.value);

		property = configOld.get("structures.schemata.disabled", CATEGORY_COMMON, "");
		property.comment = "add schemata keys to disable them. current keys: alveary3x3;farm3x3;farm3x4;farm3x5;farm4x4;farm5x5";
		disabledStructures.addAll(Arrays.asList(parseStructureKeys(property.value)));
		for (String str : disabledStructures) {
			Proxies.log.finer("Disabled structure '%s'.", str);
		}
	}

	private static void loadNewConfigFluids(File configFile) {
		configFluid = new LocalizedConfiguration(configFile, "1.0.0");

		for (Fluids fluid : Fluids.forestryFluids) {
			String fluidName = StatCollector.translateToLocal("fluid." + fluid.getTag());

			boolean enabledFluid = !Config.disabledFluids.contains(fluid.getTag());
			String enableFluidComment = StringUtil.localizeAndFormatRaw("for.config.fluids.enable.format", fluidName);
			enabledFluid = configFluid.getBoolean("enableFluid", fluid.getTag(), enabledFluid, enableFluidComment);
			if (!enabledFluid) {
				Config.disabledFluids.add(fluid.getTag());
			}

			boolean enabledFluidBlock = !Config.disabledBlocks.contains(fluid.getTag());
			String enableFluidBlockComment = StringUtil.localizeAndFormatRaw("for.config.fluid.blocks.enable.format", fluidName);
			enabledFluidBlock = configFluid.getBoolean("enableFluidBlock", fluid.getTag(), enabledFluidBlock, enableFluidBlockComment);
			if (!enabledFluidBlock) {
				Config.disabledBlocks.add(fluid.getTag());
			}
		}

		configFluid.save();
	}

	private static void loadOldConfigFluids() {
		forestry.core.config.deprecated.Property property;
		for (Fluids fluid : Fluids.forestryFluids) {
			property = configOld.get("disable.fluid." + fluid.getTag(), CATEGORY_FLUIDS, false);
			property.comment = "set to true to disable the fluid for " + fluid.getTag();
			if (Boolean.parseBoolean(property.value)) {
				Config.disabledFluids.add(fluid.getTag());
			}

			property = configOld.get("disable.fluidBlock." + fluid.getTag(), CATEGORY_FLUIDS, false);
			property.comment = "set to true to disable the in-world FluidBlock for " + fluid.getTag();
			if (Boolean.parseBoolean(property.value)) {
				Config.disabledBlocks.add(fluid.getTag());
			}
		}
	}

	private static void CopyFileToFS(File destination, String resourcePath) {
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void loadHints() {

		Properties prop = new Properties();

		try {
			InputStream hintStream = Config.class.getResourceAsStream("/config/forestry/hints.properties");
			prop.load(hintStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (String key : prop.stringPropertyNames()) {
			hints.put(key, parseHints(prop.getProperty(key)));
		}
	}

	private static String[] parseHints(String list) {
		if (list.isEmpty()) {
			return new String[0];
		} else {
			return list.split("[;]+");
		}
	}

	private static String[] parseStructureKeys(String list) {
		if (list.isEmpty()) {
			return new String[0];
		} else {
			return list.split("[;]+");
		}

	}

	private static String[] parseStamps(String list) {
		if (list.isEmpty()) {
			return new String[0];
		} else {
			return list.split("[;]+");
		}
	}
}
