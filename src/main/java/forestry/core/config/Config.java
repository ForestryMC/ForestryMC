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

import com.google.common.collect.LinkedListMultimap;
import forestry.Forestry;
import forestry.core.fluids.Fluids;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.mail.gui.GuiMailboxInfo;
import net.minecraftforge.common.config.Property;

public class Config {

	public static final String CATEGORY_COMMON = "common";
	public static final String CATEGORY_FLUIDS = "fluids";
	public static final String CATEGORY_FARM = "farm";

	public static LocalizedConfiguration configCommon;
	public static LocalizedConfiguration configFluid;

	public static String gameMode;

	private static final Set<String> disabledStructures = new HashSet<>();
	private static final Set<String> disabledFluids = new HashSet<>();
	private static final Set<String> disabledBlocks = new HashSet<>();

	public static boolean isDebug = false;

	public static boolean enablePermissions = true;

	// Graphics
	public static boolean enableParticleFX = true;

	//Humus
	public static int humusDegradeDelimiter = 3;
	
	// Genetics
	public static boolean pollinateVanillaTrees = true;
	public static float researchMutationBoostMultiplier = 1.5f;
	public static float maxResearchMutationBoostPercent = 5.0f;

	// World generation
	public static boolean generateApatiteOre = true;
	public static boolean generateCopperOre = true;
	public static boolean generateTinOre = true;
	private static float generateBeehivesAmount = 1.0f;
	public static boolean generateBeehivesDebug = false;
	public static boolean logHivePlacement = false;
	public static boolean enableVillagers = true;

	// Retrogen
	public static boolean doRetrogen = false;
	public static boolean forceRetrogen = false;

	// Performance
	public static boolean enableBackpackResupply = true;

	// Customization
	private static boolean craftingBronzeEnabled = true;

	public static boolean craftingStampsEnabled = true;
	public static final ArrayList<String> collectorStamps = new ArrayList<>();

	public static int farmSize = 2;
	public static boolean squareFarms = false;
	private static boolean enableExUtilEnderLily = true;
	private static boolean enableExUtilRedOrchid = true;
	private static boolean enableMagicalCropsSupport = true;

	// Mail
	public static boolean mailAlertEnabled = true;
	public static GuiMailboxInfo.XPosition mailAlertXPosition = GuiMailboxInfo.XPosition.LEFT;
	public static GuiMailboxInfo.YPosition mailAlertYPosition = GuiMailboxInfo.YPosition.TOP;

	// Gui tabs (Ledger)
	public static int guiTabSpeed = 8;

	// Hints
	public static boolean enableHints = true;
	public static final LinkedListMultimap<String, String> hints = LinkedListMultimap.create();
	public static boolean enableEnergyStat = true;

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

	public static boolean isExUtilRedOrchidEnabled() { return enableExUtilRedOrchid; }

	public static boolean isMagicalCropsSupportEnabled() {
		return enableMagicalCropsSupport;
	}

	public static void load() {
		File configCommonFile = new File(Forestry.instance.getConfigFolder(), CATEGORY_COMMON + ".cfg");
		loadConfigCommon(configCommonFile);

		File configFluidsFile = new File(Forestry.instance.getConfigFolder(), CATEGORY_FLUIDS + ".cfg");
		loadConfigFluids(configFluidsFile);

		loadHints();
	}

	private static void loadConfigCommon(File configFileCommon) {

		configCommon = new LocalizedConfiguration(configFileCommon, "1.2.0");

		gameMode = configCommon.getStringLocalized("difficulty", "game.mode", "EASY", new String[]{"OP, EASY, NORMAL, HARD"});

		boolean recreate = configCommon.getBooleanLocalized("difficulty", "recreate.definitions", true);
		if (recreate) {
			Log.info("Recreating all gamemode definitions from the defaults. This may be caused by an upgrade");

			String recreateDefinitionsComment = Translator.translateToLocal("for.config.difficulty.recreate.definitions.comment");
			Property property = configCommon.get("difficulty", "recreate.definitions", true, recreateDefinitionsComment);
			property.set(false);

			// Make sure the default mode files are there.

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

			Log.info("Enabled force retrogen.");
			doRetrogen = true;
		} else if (doRetrogen) {
			Log.info("Enabled retrogen.");
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
		} catch (RuntimeException ex) {
			Log.warning("Failed to read config for 'crafting.stamps.disabled', setting to default.");
			Property property = configCommon.get("crafting.stamps", "disabled", defaultCollectors);
			property.setToDefault();
			collectorStamps.addAll(Arrays.asList(defaultCollectors));
		}

		pollinateVanillaTrees = configCommon.getBooleanLocalized("genetics", "pollinate.vanilla.trees", pollinateVanillaTrees);
		researchMutationBoostMultiplier = configCommon.getFloatLocalized("genetics.research.boost", "multiplier", researchMutationBoostMultiplier, 1.0f, 1000.f);
		maxResearchMutationBoostPercent = configCommon.getFloatLocalized("genetics.research.boost", "max.percent", maxResearchMutationBoostPercent, 0.0f, 100.0f);

		enableBackpackResupply = configCommon.getBooleanLocalized("performance", "backpacks.resupply", enableBackpackResupply);

		humusDegradeDelimiter = configCommon.getIntLocalized("tweaks.humus", "degradeDelimiter", humusDegradeDelimiter, 1, 10);
		
		mailAlertEnabled = configCommon.getBooleanLocalized("tweaks.gui.mail.alert", "enabled", mailAlertEnabled);
		mailAlertXPosition = configCommon.getEnumLocalized("tweaks.gui.mail.alert", "xPosition", mailAlertXPosition, GuiMailboxInfo.XPosition.values());
		mailAlertYPosition = configCommon.getEnumLocalized("tweaks.gui.mail.alert", "yPosition", mailAlertYPosition, GuiMailboxInfo.YPosition.values());

		guiTabSpeed = configCommon.getIntLocalized("tweaks.gui.tabs", "speed", guiTabSpeed, 1, 50);
		enableHints = configCommon.getBooleanLocalized("tweaks.gui.tabs", "hints", enableHints);
		enableEnergyStat = configCommon.getBooleanLocalized("tweaks.gui.tabs", "energy", enableEnergyStat);

		enablePermissions = configCommon.getBooleanLocalized("tweaks", "permissions", enablePermissions);

		farmSize = configCommon.getIntLocalized("tweaks.farms", "size", farmSize, 1, 3);
		squareFarms = configCommon.getBooleanLocalized("tweaks.farms", "square", squareFarms);
		enableExUtilEnderLily = configCommon.getBooleanLocalized("tweaks.farms", "enderlily", enableExUtilEnderLily);
		enableExUtilRedOrchid = configCommon.getBooleanLocalized("tweaks.farms", "redorchid", enableExUtilRedOrchid);
		enableMagicalCropsSupport = configCommon.getBooleanLocalized("tweaks.farms", "magicalcrops", enableMagicalCropsSupport);

		String[] availableStructures = new String[]{"alveary3x3", "farm3x3", "farm3x4", "farm3x5", "farm4x4", "farm5x5"};
		String[] disabledStructureArray = disabledStructures.toArray(new String[disabledStructures.size()]);
		disabledStructureArray = configCommon.getStringListLocalized("structures", "disabled", disabledStructureArray, availableStructures);

		disabledStructures.addAll(Arrays.asList(disabledStructureArray));
		for (String str : disabledStructures) {
			Log.debug("Disabled structure '%s'.", str);
		}

		isDebug = configCommon.getBooleanLocalized("debug", "enabled", isDebug);

		configCommon.save();
	}

	private static void loadConfigFluids(File configFile) {
		configFluid = new LocalizedConfiguration(configFile, "1.0.0");
		
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
			if (parsedHints != null) {
				for (String parsedHint : parsedHints) {
					hints.put(key, parsedHint);
				}
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
