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

import forestry.Forestry;
import forestry.api.core.IGameMode;
import forestry.core.utils.Log;

public class GameMode implements IGameMode {
	private static final String GAMEMODE_KEY = "gamemode";
	private String identifier = "EASY";

	private final Map<String, Boolean> booleanSettings = new HashMap<>();
	private final Map<String, Integer> integerSettings = new HashMap<>();
	private final Map<String, Float> floatSettings = new HashMap<>();

	private static final float ENERGY_DEMAND_MODIFIER = 1.0f;
	private static final float FUEL_MODIFIER = 1.0f;

	private static final int FARM_FERTILIZER_MODIFIER = 4;

	private static final int recipeFertilizerOutputApatite = 8;
	private static final int recipeFertilizerOutputAsh = 16;
	private static final int recipeCompostOutputWheat = 4;
	private static final int recipeCompostOutputAsh = 1;

	private static final int recipeHumusOutputFertilizer = 8;
	private static final int recipeHumusOutputCompost = 8;

	private static final int recipeBogEarthOutputBucket = 6;
	private static final int recipeBogEarthOutputCans = 8;

	private static final int recipeCanOutput = 12;
	private static final int recipeCapsuleOutput = 4;
	private static final int recipeRefractoryOutput = 4;

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
		String category = "gamemodes/" + identifier;

		File configFile = new File(Forestry.instance.getConfigFolder(), category + ".cfg");

		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");

		initSettingFloat(config, "energy", "demand.modifier", ENERGY_DEMAND_MODIFIER);
		initSettingBoolean(config, "energy", "engine.clockwork", true);

		initSettingInt(config, "farms", "fertilizer.modifier", FARM_FERTILIZER_MODIFIER);

		initSettingFloat(config, "fuel.ethanol", "generator", FUEL_MODIFIER);
		initSettingFloat(config, "fuel.ethanol", "combustion", FUEL_MODIFIER);
		initSettingFloat(config, "fuel.biomass", "generator", FUEL_MODIFIER);
		initSettingFloat(config, "fuel.biomass", "biogas", FUEL_MODIFIER);

		initSettingInt(config, "recipe.output.fertilizer", "apatite", recipeFertilizerOutputApatite);
		initSettingInt(config, "recipe.output.fertilizer", "ash", recipeFertilizerOutputAsh);
		initSettingInt(config, "recipe.output.compost", "wheat", recipeCompostOutputWheat);
		initSettingInt(config, "recipe.output.compost", "ash", recipeCompostOutputAsh);
		initSettingInt(config, "recipe.output.humus", "fertilizer", recipeHumusOutputFertilizer);
		initSettingInt(config, "recipe.output.humus", "compost", recipeHumusOutputCompost);
		initSettingInt(config, "recipe.output.bogearth", "bucket", recipeBogEarthOutputBucket);
		initSettingInt(config, "recipe.output.bogearth", "can", recipeBogEarthOutputCans);
		initSettingInt(config, "recipe.output", "can", recipeCanOutput);
		initSettingInt(config, "recipe.output", "capsule", recipeCapsuleOutput);
		initSettingInt(config, "recipe.output", "refractory", recipeRefractoryOutput);

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
	public String getIdentifier() {
		return identifier;
	}

}
