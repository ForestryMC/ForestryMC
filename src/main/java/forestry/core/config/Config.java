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

import forestry.core.proxy.Proxies;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

public class Config {

	public static final String CATEGORY_COMMON = "common";
	public static final String CATEGORY_DEBUG = "debug";

	public static Configuration config;

	public static String gameMode;

	public static final HashSet<String> disabledStructures = new HashSet<String>();

	public static boolean isDebug = false;

	public static boolean disablePermissions = false;
	public static boolean disableNags = false;
	public static boolean disableVersionCheck = false;

	public static boolean invalidFingerprint = false;

	// Graphics
	public static boolean enableParticleFX = true;

	// Bees
	public static boolean clearInvalidChromosomes = false;

	// Dungeon loot
	public static boolean dungeonLootRare = false;

	// World generation
	public static boolean generateApatiteOre = true;
	public static boolean generateCopperOre = true;
	public static boolean generateTinOre = true;
	public static boolean generateBeehives = true;
	public static boolean generateBogEarth = false;
	public static boolean generateBeehivesDebug = false;

	// Performance
	public static boolean enableBackpackResupply = true;
	public static int planterThrottle;
	public static int harvesterThrottle;

	// Customization
	public static boolean tooltipLiquidAmount = true;
	public static boolean planterSideSensitive = true;
	/**
	 * True if the harvester should decide output to pipes according to side.
	 */
	public static boolean harvesterSideSensitive = true;

	/* GO AWAY GRED */
	private static boolean craftingBronzeEnabled = true;

	public static boolean getCraftingBronzeEnabled() {
		return craftingBronzeEnabled;
	}

	public static boolean craftingStampsEnabled = true;
	public static final ArrayList<String> collectorStamps = new ArrayList<String>();

	public static boolean applePickup = true;
	public static boolean squareFarms = false;

	// Mail
	public static boolean mailAlertEnabled = true;

	// Hints
	public static boolean disableHints = false;
	public static final HashMap<String, String[]> hints = new HashMap<String, String[]>();
	public static boolean disableEnergyStat = false;

	public static void load() {

		setDebugMode();

		config = new Configuration();
		config.addPurge("buildcraft.blockid.engine");
		config.addPurge("buildcraft.blockid.pipe");
		config.addPurge("crafting.farms.enabled");
		config.addPurge("crafting.farms.uncrafting.enabled");
		config.addPurge("performance.planter");
		config.addPurge("performance.harvester");
		config.addPurge("buildcraft.ignore");
		config.addPurge("power.framework");

		Property property = config.get("difficulty.gamemode", CATEGORY_COMMON, "EASY");
		property.Comment = "set to your preferred game mode. available modes are OP, EASY, NORMAL, HARD. mismatch with the server may cause visual glitches with recipes. setting an unavailable mode will create a new mode definition file.";
		gameMode = property.Value;

		property = config.get("difficulty.recreate.definitions", CATEGORY_COMMON, true);
		property.Comment = "set to true to force recreation of the game mode definitions in config/forestry/gamemodes";
		boolean recreate = Boolean.parseBoolean(property.Value);

		if (recreate)
			Proxies.log.info("Recreating all gamemode definitions from the defaults. This may be caused by an upgrade");

		// Make sure the default mode files are there.
		File easyMode = config.getCategoryFile("gamemodes/EASY");
		if (recreate)
			CopyFileToFS(easyMode, "/config/forestry/gamemodes/EASY.conf");

		File opMode = config.getCategoryFile("gamemodes/OP");
		if (!opMode.exists() || recreate)
			CopyFileToFS(opMode, "/config/forestry/gamemodes/OP.conf");

		File normalMode = config.getCategoryFile("gamemodes/NORMAL");
		if (!normalMode.exists() || recreate)
			CopyFileToFS(normalMode, "/config/forestry/gamemodes/NORMAL.conf");

		File hardMode = config.getCategoryFile("gamemodes/HARD");
		if (!hardMode.exists() || recreate)
			CopyFileToFS(hardMode, "/config/forestry/gamemodes/HARD.conf");

		config.set("difficulty.recreate.definitions", CATEGORY_COMMON, false);

		Property particleFX = config.get("performance.particleFX.enabled", CATEGORY_COMMON, true);
		particleFX.Comment = "set to false to disable particle fx on slower machines";
		enableParticleFX = Boolean.parseBoolean(particleFX.Value);

		Property genApatiteOre = config.get("world.generate.apatite", CATEGORY_COMMON, true);
		genApatiteOre.Comment = "set to false to force forestry to skip generating own apatite ore blocks in the world";
		generateApatiteOre = Boolean.parseBoolean(genApatiteOre.Value);

		Property genBeehives = config.get("world.generate.beehives", CATEGORY_COMMON, true);
		genBeehives.Comment = "set to false to force forestry to skip generating beehives in the world";
		generateBeehives = Boolean.parseBoolean(genBeehives.Value);

		Property genDebugBeehives = config.get("world.generate.beehives.debug", CATEGORY_DEBUG, false);
		genDebugBeehives.Comment = "Set to true to force Forestry to try to generate a beehive at every possible location.";
		generateBeehivesDebug = Boolean.parseBoolean(genDebugBeehives.Value);

		Property genCopperOre = config.get("world.generate.copper", CATEGORY_COMMON, true);
		genCopperOre.Comment = "set to false to force forestry to skip generating own copper ore blocks in the world";
		generateCopperOre = Boolean.parseBoolean(genCopperOre.Value);

		Property genTinOre = config.get("world.generate.tin", CATEGORY_COMMON, true);
		genTinOre.Comment = "set to false to force forestry to skip generating own tin ore blocks in the world";
		generateTinOre = Boolean.parseBoolean(genTinOre.Value);

		Property planterSideSense = config.get("planters.sidesensitive", CATEGORY_COMMON, true);
		planterSideSense.Comment = "set to false if farms should output all harvested products regardless of side a pipe is attached to";
		planterSideSensitive = Boolean.parseBoolean(planterSideSense.Value);
		Property harvesterSideSense = config.get("harvesters.sidesensitive", CATEGORY_COMMON, true);
		harvesterSideSense.Comment = "set to false if harvesters should output all harvested products regardless of side a pipe is attached to";
		harvesterSideSensitive = Boolean.parseBoolean(harvesterSideSense.Value);

		Property bronzeRecipe = config.get("crafting.bronze.enabled", CATEGORY_COMMON, true);
		bronzeRecipe.Comment = "set to false to disable crafting recipe for bronze";
		craftingBronzeEnabled = Boolean.parseBoolean(bronzeRecipe.Value);

		property = config.get("crafting.stamps.enabled", CATEGORY_COMMON, true);
		property.Comment = "set to false to disable crafting recipes for all types of stamps.";
		craftingStampsEnabled = Boolean.parseBoolean(property.Value);

		String defaultCollectors = "20n;50n;100n;200n";
		property = config.get("crafting.stamps.collector", CATEGORY_COMMON, defaultCollectors);
		property.Comment = "if crafting of stamps is generally allowed, these stamps are still excluded from crafting.";
		try {
			collectorStamps.addAll(Arrays.asList(parseStamps(property.Value)));
		} catch(Exception ex) {
			config.set("crafting.stamps.collector", CATEGORY_COMMON, defaultCollectors);
			collectorStamps.addAll(Arrays.asList(parseStamps(defaultCollectors)));
		}

		Property indicatorEnable = config.get("tweaks.mailalert.enabled", CATEGORY_COMMON, true);
		indicatorEnable.Comment = "set to false to disable the mail alert box";
		mailAlertEnabled = Boolean.parseBoolean(indicatorEnable.Value);

		Property appleHarvest = config.get("tweaks.apple.pickup.enabled", CATEGORY_COMMON, true);
		appleHarvest.Comment = "set to false to disable apple pickup by appropriate harvesters";
		applePickup = Boolean.parseBoolean(appleHarvest.Value);

		Property clearGenome = config.get("genetics.clear.invalid.chromosomes", CATEGORY_COMMON, clearInvalidChromosomes);
		clearGenome.Comment = "set to true to clear chromosomes which contain invalid alleles. might rescue your save if it is crashing after the removal of a bee addon.";
		clearInvalidChromosomes = Boolean.parseBoolean(clearGenome.Value);

		Property dungeonLootRarity = config.get("difficulty.dungeonloot.rare", CATEGORY_COMMON, false);
		dungeonLootRarity.Comment = "set to true to make dungeon loot generated by forestry rarer";
		dungeonLootRare = Boolean.parseBoolean(dungeonLootRarity.Value);

		Property resupplyEnable = config.get("performance.backpacks.resupply", CATEGORY_COMMON, true);
		resupplyEnable.Comment = "leaving this enabled will cycle the list of active players PER INGAME TICK to check for resupply via backpack. you want to set this to false on busy servers.";
		Config.enableBackpackResupply = Boolean.parseBoolean(resupplyEnable.Value);

		property = config.get("tweaks.hints.disabled", CATEGORY_COMMON, false);
		property.Comment = "set to true to disable hints on machine and engine guis.";
		Config.disableHints = Boolean.parseBoolean(property.Value);
		property = config.get("tweaks.energystat.disabled", CATEGORY_COMMON, true);
		property.Comment = "set to true to disable energy statistics on energy consumers.";
		Config.disableEnergyStat = !isDebug && Boolean.parseBoolean(property.Value);
		property = config.get("tweaks.tooltip.liquidamount.disabled", CATEGORY_COMMON, false);
		property.Comment = "set to true to disable displaying liquid amounts in tank tooltips.";
		Config.tooltipLiquidAmount = isDebug || !Boolean.parseBoolean(property.Value);

		property = config.get("tweaks.permissions.disabled", CATEGORY_COMMON, false);
		property.Comment = "set to true to disable access restrictions on forestry machines.";
		Config.disablePermissions = Boolean.parseBoolean(property.Value);

		property = config.get("tweaks.nags.disabled", CATEGORY_COMMON, false);
		property.Comment = "set to true to disable some nagging and warnings (e.g. GregTech).";
		Config.disableNags = Boolean.parseBoolean(property.Value);

		property = config.get("tweaks.upgradenotice.disabled", CATEGORY_COMMON, false);
		property.Comment = "set to true to disable update and version check notice.";
		Config.disableVersionCheck = isDebug || Boolean.parseBoolean(property.Value);

		property = config.get("tweaks.farms.squared", CATEGORY_COMMON, false);
		property.Comment = "set to true to have farms use a square layout instead of a diamond one.";
		Config.squareFarms = Boolean.parseBoolean(property.Value);

		property = config.get("structures.schemata.disabled", CATEGORY_COMMON, "");
		property.Comment = "add schemata keys to disable them. current keys: alveary3x3;farm3x3;farm3x4;farm3x5;farm4x4;farm5x5";
		disabledStructures.addAll(Arrays.asList(parseStructureKeys(property.Value)));
		for (String str : disabledStructures)
			Proxies.log.finer("Disabled structure '%s'.", str);

		config.save();

		loadHints();
	}

	private static void setDebugMode() {
		File debug = new File(Proxies.common.getForestryRoot(), "config/" + Defaults.MOD.toLowerCase(Locale.ENGLISH) + "/DEBUG.ON");
		isDebug = debug.exists();
	}

	private static void CopyFileToFS(File destination, String resourcePath) {
		InputStream stream = Config.class.getResourceAsStream(resourcePath);
		OutputStream outstream;
		int readBytes;
		byte[] buffer = new byte[4096];
		try {

			if (destination.getParentFile() != null)
				destination.getParentFile().mkdirs();

			if (!destination.exists() && !destination.createNewFile())
				return;

			outstream = new FileOutputStream(destination);
			while ((readBytes = stream.read(buffer)) > 0)
				outstream.write(buffer, 0, readBytes);
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

		for (String key : prop.stringPropertyNames())
			hints.put(key, parseHints(prop.getProperty(key)));
	}

	private static String[] parseHints(String list) {
		if (list.isEmpty())
			return new String[0];
		else
			return list.split("[;]+");
	}

	private static String[] parseStructureKeys(String list) {
		if (list.isEmpty())
			return new String[0];
		else
			return list.split("[;]+");

	}

	private static String[] parseStamps(String list) {
		if (list.isEmpty())
			return new String[0];
		else
			return list.split("[;]+");
	}

	public static boolean getOrCreateBooleanProperty(String key, String kind, boolean defaults) {
		return Boolean.parseBoolean(config.get(key, kind, defaults).Value);
	}

}
