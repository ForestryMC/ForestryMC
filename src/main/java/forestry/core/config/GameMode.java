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
import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.Forestry;
import forestry.api.core.IGameMode;
import forestry.core.blocks.BlockSoil;
import forestry.core.utils.Log;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginFluids;

public class GameMode implements IGameMode {
	private static final String GAMEMODE_KEY = "gamemode";
	private String identifier = "EASY";
	private final String category;

	private final Map<String, Boolean> booleanSettings = new HashMap<>();
	private final Map<String, Integer> integerSettings = new HashMap<>();
	private final Map<String, Float> floatSettings = new HashMap<>();
	private final Map<String, ItemStack> stackSettings = new HashMap<>();

	private static final float ENERGY_DEMAND_MODIFIER = 1.0f;
	private static final float FUEL_MODIFIER = 1.0f;

	private static final int FARM_FERTILIZER_VALUE = 2000;

	private final ItemStack recipeFertilizerOutputApatite = PluginCore.items.fertilizerCompound.getItemStack(8);
	private final ItemStack recipeFertilizerOutputAsh = PluginCore.items.fertilizerCompound.getItemStack(16);
	private final ItemStack recipeCompostOutputWheat = PluginCore.items.fertilizerBio.getItemStack(4);
	private final ItemStack recipeCompostOutputAsh = PluginCore.items.fertilizerBio.getItemStack(1);

	private final ItemStack recipeHumusOutputFertilizer = PluginCore.blocks.soil.get(BlockSoil.SoilType.HUMUS, 8);
	private final ItemStack recipeHumusOutputCompost = PluginCore.blocks.soil.get(BlockSoil.SoilType.HUMUS, 8);

	private final ItemStack recipeBogEarthOutputBucket = PluginCore.blocks.soil.get(BlockSoil.SoilType.BOG_EARTH, 6);
	private final ItemStack recipeBogEarthOutputCans = PluginCore.blocks.soil.get(BlockSoil.SoilType.BOG_EARTH, 8);

	private final ItemStack recipeCanOutput = PluginFluids.items.canEmpty.getItemStack(12);
	private final ItemStack recipeCapsuleOutput = PluginFluids.items.waxCapsuleEmpty.getItemStack(4);
	private final ItemStack recipeRefractoryOutput = PluginFluids.items.refractoryEmpty.getItemStack(4);

	private static final int FERMENTATION_DURATION_FERTILIZER = 200;
	private static final int FERMENTATION_DURATION_COMPOST = 250;

	private static final int FERMENTED_CYCLE_FERTILIZER = 56;
	private static final int FERMENTED_CYCLE_COMPOST = 48;

	private static final int FERMENTED_SAPLING = 250;
	private static final int FERMENTED_CACTI = 50;
	private static final int FERMENTED_WHEAT = 50;
	private static final int FERMENTED_CANE = 50;
	private static final int FERMENTED_MUSHROOM = 50;

	private static final int SQUEEZED_LIQUID_SEED = 10;
	private static final int SQUEEZED_LIQUID_APPLE = 200;
	private static final int SQUEEZED_MULCH_APPLE = 20;

	public GameMode(String identifier) {

		this.identifier = identifier;
		this.category = "gamemodes/" + identifier;

		File configFile = new File(Forestry.instance.getConfigFolder(), category + ".cfg");

		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");

		initSettingFloat(config, "energy", "demand.modifier", ENERGY_DEMAND_MODIFIER);
		initSettingBoolean(config, "energy", "engine.clockwork", true);

		initSettingInt(config, "farms", "fertilizer.value", FARM_FERTILIZER_VALUE);

		initSettingFloat(config, "fuel.ethanol", "generator", FUEL_MODIFIER);
		initSettingFloat(config, "fuel.ethanol", "combustion", FUEL_MODIFIER);
		initSettingFloat(config, "fuel.biomass", "generator", FUEL_MODIFIER);
		initSettingFloat(config, "fuel.biomass", "biogas", FUEL_MODIFIER);

		initSettingStack(config, "recipe.output.fertilizer", "apatite", recipeFertilizerOutputApatite);
		initSettingStack(config, "recipe.output.fertilizer", "ash", recipeFertilizerOutputAsh);
		initSettingStack(config, "recipe.output.compost", "wheat", recipeCompostOutputWheat);
		initSettingStack(config, "recipe.output.compost", "ash", recipeCompostOutputAsh);
		initSettingStack(config, "recipe.output.humus", "fertilizer", recipeHumusOutputFertilizer);
		initSettingStack(config, "recipe.output.humus", "compost", recipeHumusOutputCompost);
		initSettingStack(config, "recipe.output.bogearth", "bucket", recipeBogEarthOutputBucket);
		initSettingStack(config, "recipe.output.bogearth", "can", recipeBogEarthOutputCans);
		initSettingStack(config, "recipe.output", "can", recipeCanOutput);
		initSettingStack(config, "recipe.output", "capsule", recipeCapsuleOutput);
		initSettingStack(config, "recipe.output", "refractory", recipeRefractoryOutput);

		initSettingInt(config, "fermenter.cycles", "fertilizer", FERMENTATION_DURATION_FERTILIZER);
		initSettingInt(config, "fermenter.cycles", "compost", FERMENTATION_DURATION_COMPOST);

		initSettingInt(config, "fermenter.value", "fertilizer", FERMENTED_CYCLE_FERTILIZER);
		initSettingInt(config, "fermenter.value", "compost", FERMENTED_CYCLE_COMPOST);

		initSettingInt(config, "fermenter.yield", "sapling", FERMENTED_SAPLING);
		initSettingInt(config, "fermenter.yield", "cactus", FERMENTED_CACTI);
		initSettingInt(config, "fermenter.yield", "wheat", FERMENTED_WHEAT);
		initSettingInt(config, "fermenter.yield", "cane", FERMENTED_CANE);
		initSettingInt(config, "fermenter.yield", "mushroom", FERMENTED_MUSHROOM);

		initSettingInt(config, "squeezer.liquid", "seed", SQUEEZED_LIQUID_SEED);
		initSettingInt(config, "squeezer.liquid", "apple", SQUEEZED_LIQUID_APPLE);

		initSettingInt(config, "squeezer.mulch", "apple", SQUEEZED_MULCH_APPLE);

		config.save();
	}

	private void initSettingFloat(LocalizedConfiguration config, String category, String name, float defaultValue) {
		String fullName = category + '.' + name;
		float floatValue = config.getFloatLocalized(GAMEMODE_KEY + '.' + category, name, defaultValue, 0.0f, 10.0f);
		floatSettings.put(fullName, floatValue);
	}

	private void initSettingInt(LocalizedConfiguration config, String category, String key, int defaultValue) {
		String fullKey = category + '.' + key;
		int intValue = config.getIntLocalized(GAMEMODE_KEY + '.' + category, key, defaultValue, 0, 2000);
		integerSettings.put(fullKey, intValue);
	}

	private void initSettingStack(LocalizedConfiguration config, String category, String key, ItemStack defaultValue) {
		String fullKey = category + '.' + key;
		int stackSize = config.getIntLocalized(GAMEMODE_KEY + '.' + category, key, defaultValue.stackSize, 0, 64);
		ItemStack changed = defaultValue.copy();
		changed.stackSize = stackSize;
		stackSettings.put(fullKey, changed);
	}

	private void initSettingBoolean(LocalizedConfiguration config, String category, String key, boolean defaultValue) {
		String fullKey = category + '.' + key;
		boolean booleanValue = config.getBooleanLocalized(GAMEMODE_KEY + '.' + category, key, defaultValue);
		booleanSettings.put(fullKey, booleanValue);
	}

	@Override
	public int getIntegerSetting(String ident) {
		if (integerSettings.containsKey(ident)) {
			return integerSettings.get(ident);
		}
		Log.warning("No such setting: " + ident);
		return -1;
	}

	@Override
	public float getFloatSetting(String ident) {
		if (floatSettings.containsKey(ident)) {
			return floatSettings.get(ident);
		}
		Log.warning("No such setting: " + ident);
		return 1;
	}

	@Override
	public boolean getBooleanSetting(String ident) {
		if (booleanSettings.containsKey(ident)) {
			return booleanSettings.get(ident);
		}
		Log.warning("No such setting: " + ident);
		return false;
	}

	@Override
	public ItemStack getStackSetting(String ident) {
		if (stackSettings.containsKey(ident)) {
			return stackSettings.get(ident);
		}
		Log.warning("No such setting: " + ident);
		return new ItemStack(Items.apple, 1);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

}
