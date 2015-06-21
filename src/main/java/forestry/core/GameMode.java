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
package forestry.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import forestry.Forestry;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IGameMode;
import forestry.core.config.Config;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;

public class GameMode implements IGameMode {

	public static IGameMode getGameMode() {

		if (ForestryAPI.activeMode == null) {
			ForestryAPI.activeMode = new GameMode(Config.gameMode);
		}

		return ForestryAPI.activeMode;
	}

	private String identifier = "EASY";
	private final String category;

	private final Map<String, Boolean> booleanSettings = new HashMap<String, Boolean>();
	private final Map<String, Integer> integerSettings = new HashMap<String, Integer>();
	private final Map<String, Float> floatSettings = new HashMap<String, Float>();
	private final Map<String, ItemStack> stackSettings = new HashMap<String, ItemStack>();

	private static final float ENERGY_DEMAND_MODIFIER = 1.0f;
	private static final float FUEL_MODIFIER = 1.0f;

	private static final int FARM_FERTILIZER_VALUE = 2000;

	private final ItemStack recipeFertilizerOutputApatite = ForestryItem.fertilizerCompound.getItemStack(8);
	private final ItemStack recipeFertilizerOutputAsh = ForestryItem.fertilizerCompound.getItemStack(16);
	private final ItemStack recipeCompostOutputWheat = ForestryItem.fertilizerBio.getItemStack(4);
	private final ItemStack recipeCompostOutputAsh = ForestryItem.fertilizerBio.getItemStack(1);

	private final ItemStack recipeHumusOutputFertilizer = ForestryBlock.soil.getItemStack(8);
	private final ItemStack recipeHumusOutputCompost = ForestryBlock.soil.getItemStack(8);

	private final ItemStack recipeBogEarthOutputBucket = ForestryBlock.soil.getItemStack(6, 1);
	private final ItemStack recipeBogEarthOutputCans = ForestryBlock.soil.getItemStack(8, 1);

	private final ItemStack recipeCanOutput = ForestryItem.canEmpty.getItemStack(12);
	private final ItemStack recipeCapsuleOutput = ForestryItem.waxCapsule.getItemStack(4);
	private final ItemStack recipeRefractoryOutput = ForestryItem.refractoryEmpty.getItemStack(4);

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

		final String oldConfig = category + ".conf";
		final String newConfig = category + ".cfg";
		File newConfigFile = new File(Forestry.instance.getConfigFolder(), newConfig);
		File oldConfigFile = new File(Forestry.instance.getConfigFolder(), oldConfig);
		if (oldConfigFile.exists()) {
			loadOldConfig();

			final String oldConfigRenamed = category + ".conf.old";
			File oldConfigFileRenamed = new File(Forestry.instance.getConfigFolder(), oldConfigRenamed);
			if (oldConfigFile.renameTo(oldConfigFileRenamed)) {
				Proxies.log.info("Migrated " + category + " settings to the new file '" + newConfig + "' and renamed '" + oldConfig + "' to '" + oldConfigRenamed + "'.");
			}
		}

		Configuration config = new Configuration(newConfigFile);

		initSettingFloat(config, "energy", "demand.modifier", ENERGY_DEMAND_MODIFIER, "modifies the energy required to activate machines, as well as the max amount of energy stored and accepted.");
		initSettingInt(config, "farms", "fertilizer.value", FARM_FERTILIZER_VALUE, "modifies the time a piece of fertilizer lasts in a farm.");

		initSettingFloat(config, "fuel.ethanol", "generator", FUEL_MODIFIER, "modifies the energy provided by ethanol in a Bio Generator.");
		initSettingFloat(config, "fuel.ethanol", "combustion", FUEL_MODIFIER, "modifies the energy provided by ethanol in Buildcraft Combustion Engines.");
		initSettingFloat(config, "fuel.biomass", "generator", FUEL_MODIFIER, "modifies the energy provided by Biomass in a Bio Generator.");
		initSettingFloat(config, "fuel.biomass", "biogas", FUEL_MODIFIER, "modifies the energy provided by Biomass in Biogas Engines.");

		initSettingStack(config, "recipe.output.fertilizer", "apatite", recipeFertilizerOutputApatite, "amount of fertilizer yielded by the recipe using apatite.");
		initSettingStack(config, "recipe.output.fertilizer", "ash", recipeFertilizerOutputAsh, "amount of fertilizer yielded by the recipe using ash.");
		initSettingStack(config, "recipe.output.compost", "wheat", recipeCompostOutputWheat, "amount of compost yielded by the recipe using wheat.");
		initSettingStack(config, "recipe.output.compost", "ash", recipeCompostOutputAsh, "amount of compost yielded by the recipe using ash.");
		initSettingStack(config, "recipe.output.humus", "fertilizer", recipeHumusOutputFertilizer, "amount of humus yielded by the recipe using fertilizer.");
		initSettingStack(config, "recipe.output.humus", "compost", recipeHumusOutputCompost, "amount of humus yielded by the recipe using compost.");
		initSettingStack(config, "recipe.output.bogearth", "bucket", recipeBogEarthOutputBucket, "amount of bog earth yielded by the recipe using buckets.");
		initSettingStack(config, "recipe.output.bogearth", "can", recipeBogEarthOutputCans, "amount of bog earth yielded by the recipes using cans, cells or capsules.");

		initSettingStack(config, "recipe.output", "can", recipeCanOutput, "amount yielded by the recipe for tin cans.");
		initSettingStack(config, "recipe.output", "capsule", recipeCapsuleOutput, "amount yielded by the recipe for wax capsules.");
		initSettingStack(config, "recipe.output", "refractory", recipeRefractoryOutput, "amount yielded by the recipe for refractory capsules.");

		initSettingInt(config, "fermenter.cycles", "fertilizer", FERMENTATION_DURATION_FERTILIZER, "modifies the amount of cycles fertilizer can keep a fermenter going.");
		initSettingInt(config, "fermenter.cycles", "compost", FERMENTATION_DURATION_COMPOST, "modifies the amount of cycles compost can keep a fermenter going.");

		initSettingInt(config, "fermenter.value", "fertilizer", FERMENTED_CYCLE_FERTILIZER, "modifies the amount of biomass per cycle a fermenter will produce using fertilizer.");
		initSettingInt(config, "fermenter.value", "compost", FERMENTED_CYCLE_COMPOST, "modifies the amount of biomass per cycle a fermenter will produce using compost.");

		initSettingInt(config, "fermenter.yield", "sapling", FERMENTED_SAPLING, "modifies the base amount of biomass a sapling will yield in a fermenter, affected by sappiness trait.");
		initSettingInt(config, "fermenter.yield", "cactus", FERMENTED_CACTI, "modifies the amount of biomass a piece of cactus will yield in a fermenter.");
		initSettingInt(config, "fermenter.yield", "wheat", FERMENTED_WHEAT, "modifies the amount of biomass a piece of wheat will yield in a fermenter.");
		initSettingInt(config, "fermenter.yield", "cane", FERMENTED_CANE, "modifies the amount of biomass a piece of sugar cane will yield in a fermenter.");
		initSettingInt(config, "fermenter.yield", "mushroom", FERMENTED_MUSHROOM, "modifies the amount of biomass a mushroom will yield in a fermenter.");

		initSettingInt(config, "squeezer.liquid", "seed", SQUEEZED_LIQUID_SEED, "modifies the amount of seed oil squeezed from a single seed. other sources are based off this.");
		initSettingInt(config, "squeezer.liquid", "apple", SQUEEZED_LIQUID_APPLE, "modifies the amount of juice squeezed from a single apple. other sources are based off this.");
		initSettingInt(config, "squeezer.mulch", "apple", SQUEEZED_MULCH_APPLE, "modifies the chance of mulch per squeezed apple.");
		initSettingBoolean(config, "energy", "engine.clockwork", true, "set to false to disable the clockwork engine.");

		config.save();
	}

	private void loadOldConfig() {
		initSettingFloat_old("energy.demand.modifier", ENERGY_DEMAND_MODIFIER, -1, "modifies the energy required to activate machines, as well as the max amount of energy stored and accepted.");
		initSettingInt_old("farms.fertilizer.value", FARM_FERTILIZER_VALUE, -1, "modifies the time a piece of fertilizer lasts in a farm.");

		initSettingFloat_old("fuel.ethanol.generator", FUEL_MODIFIER, -1, "modifies the energy provided by ethanol in a Bio Generator.");
		initSettingFloat_old("fuel.ethanol.combustion", FUEL_MODIFIER, -1, "modifies the energy provided by ethanol in Buildcraft Combustion Engines.");
		initSettingFloat_old("fuel.biomass.generator", FUEL_MODIFIER, -1, "modifies the energy provided by Biomass in a Bio Generator.");
		initSettingFloat_old("fuel.biomass.biogas", FUEL_MODIFIER, -1, "modifies the energy provided by Biomass in Biogas Engines.");

		initSettingStack_old("recipe.output.fertilizer.apatite", recipeFertilizerOutputApatite, "amount of fertilizer yielded by the recipe using apatite.");
		initSettingStack_old("recipe.output.fertilizer.ash", recipeFertilizerOutputAsh, "amount of fertilizer yielded by the recipe using ash.");
		initSettingStack_old("recipe.output.compost.wheat", recipeCompostOutputWheat, "amount of compost yielded by the recipe using wheat.");
		initSettingStack_old("recipe.output.compost.ash", recipeCompostOutputAsh, "amount of compost yielded by the recipe using ash.");
		initSettingStack_old("recipe.output.humus.fertilizer", recipeHumusOutputFertilizer, "amount of humus yielded by the recipe using fertilizer.");
		initSettingStack_old("recipe.output.humus.compost", recipeHumusOutputCompost, "amount of humus yielded by the recipe using compost.");
		initSettingStack_old("recipe.output.bogearth.bucket", recipeBogEarthOutputBucket, "amount of bog earth yielded by the recipe using buckets.");
		initSettingStack_old("recipe.output.bogearth.can", recipeBogEarthOutputCans, "amount of bog earth yielded by the recipes using cans, cells or capsules.");

		initSettingStack_old("recipe.output.can", recipeCanOutput, "amount yielded by the recipe for tin cans.");
		initSettingStack_old("recipe.output.capsule", recipeCapsuleOutput, "amount yielded by the recipe for wax capsules.");
		initSettingStack_old("recipe.output.refractory", recipeRefractoryOutput, "amount yielded by the recipe for refractory capsules.");

		initSettingInt_old("fermenter.cycles.fertilizer", FERMENTATION_DURATION_FERTILIZER, -1, "modifies the amount of cycles fertilizer can keep a fermenter going.");
		initSettingInt_old("fermenter.cycles.compost", FERMENTATION_DURATION_COMPOST, -1, "modifies the amount of cycles compost can keep a fermenter going.");

		initSettingInt_old("fermenter.value.fertilizer", FERMENTED_CYCLE_FERTILIZER, -1, "modifies the amount of biomass per cycle a fermenter will produce using fertilizer.");
		initSettingInt_old("fermenter.value.compost", FERMENTED_CYCLE_COMPOST, -1, "modifies the amount of biomass per cycle a fermenter will produce using compost.");

		initSettingInt_old("fermenter.yield.sapling", FERMENTED_SAPLING, FERMENTED_SAPLING * 8, "modifies the base amount of biomass a sapling will yield in a fermenter, affected by sappiness trait.");
		initSettingInt_old("fermenter.yield.cactus", FERMENTED_CACTI, FERMENTED_CACTI * 8, "modifies the amount of biomass a piece of cactus will yield in a fermenter.");
		initSettingInt_old("fermenter.yield.wheat", FERMENTED_WHEAT, FERMENTED_WHEAT * 8, "modifies the amount of biomass a piece of wheat will yield in a fermenter.");
		initSettingInt_old("fermenter.yield.cane", FERMENTED_CANE, FERMENTED_CANE * 8, "modifies the amount of biomass a piece of sugar cane will yield in a fermenter.");
		initSettingInt_old("fermenter.yield.mushroom", FERMENTED_MUSHROOM, FERMENTED_MUSHROOM * 8, "modifies the amount of biomass a mushroom will yield in a fermenter.");

		initSettingInt_old("squeezer.liquid.seed", SQUEEZED_LIQUID_SEED, SQUEEZED_LIQUID_SEED * 8, "modifies the amount of seed oil squeezed from a single seed. other sources are based off this.");
		initSettingInt_old("squeezer.liquid.apple", SQUEEZED_LIQUID_APPLE, SQUEEZED_LIQUID_APPLE * 8, "modifies the amount of juice squeezed from a single apple. other sources are based off this.");
		initSettingInt_old("squeezer.mulch.apple", SQUEEZED_MULCH_APPLE, SQUEEZED_MULCH_APPLE * 8, "modifies the chance of mulch per squeezed apple.");
		initSettingBoolean_old("energy.engine.clockwork", true, "set to false to disable the clockwork engine.");
	}

	private void initSettingFloat(Configuration config, String category, String key, double def, String comment) {
		String fullKey = category + '.' + key;
		// legacy conversion of old format to new format
		{
			Float oldSetting = floatSettings.get(fullKey);
			if (oldSetting != null) {
				def = oldSetting;
			}
		}
		Property property = config.get(category, key, def, comment);
		floatSettings.put(fullKey, (float) property.getDouble());
	}

	private void initSettingInt(Configuration config, String category, String key, int def, String comment) {
		String fullKey = category + '.' + key;
		// legacy conversion of old format to new format
		{
			Integer oldSetting = integerSettings.get(fullKey);
			if (oldSetting != null) {
				def = oldSetting;
			}
		}
		Property property = config.get(category, key, def, comment);
		integerSettings.put(fullKey, property.getInt());
	}

	private void initSettingStack(Configuration config, String category, String key, ItemStack def, String comment) {
		String fullKey = category + '.' + key;
		// legacy conversion of old format to new format
		{
			ItemStack oldSetting = stackSettings.get(fullKey);
			if (oldSetting != null) {
				def = oldSetting;
			}
		}
		Property property = config.get(category, key, def.stackSize, comment);
		ItemStack changed = def.copy();
		changed.stackSize = property.getInt();
		stackSettings.put(fullKey, changed);
	}

	private void initSettingBoolean(Configuration config, String category, String key, boolean def, String comment) {
		String fullKey = category + '.' + key;
		// legacy conversion of old format to new format
		{
			Boolean oldSetting = booleanSettings.get(fullKey);
			if (oldSetting != null) {
				def = oldSetting;
			}
		}
		Property property = config.get(category, key, def, comment);
		booleanSettings.put(fullKey, property.getBoolean());
	}

	private void initSettingStack_old(String ident, ItemStack def, String comment) {
		forestry.core.config.deprecated.Property property = Config.configOld.get(ident, category, def.stackSize);
		property.comment = comment;
		ItemStack changed = def.copy();
		changed.stackSize = Integer.parseInt(property.value);
		stackSettings.put(ident, changed);
	}

	private void initSettingInt_old(String ident, int def, int max, String comment) {
		forestry.core.config.deprecated.Property property = Config.configOld.get(ident, category, def);
		if (max < 0) {
			property.comment = comment;
			integerSettings.put(ident, Integer.parseInt(property.value));
		} else {
			property.comment = comment + " (max: " + max + ")";
			integerSettings.put(ident, Math.min(Integer.parseInt(property.value), max));
		}
		Config.configOld.set(ident, category, integerSettings.get(ident));

	}

	private void initSettingFloat_old(String ident, float def, float max, String comment) {
		forestry.core.config.deprecated.Property property = Config.configOld.get(ident, category, def);
		if (max < 0) {
			property.comment = comment;
			floatSettings.put(ident, Float.parseFloat(property.value));
		} else {
			property.comment = comment + " (max: " + max + ")";
			floatSettings.put(ident, Math.min(Float.parseFloat(property.value), max));
		}
		Config.configOld.set(ident, category, floatSettings.get(ident));
	}

	private void initSettingBoolean_old(String ident, boolean def, String comment) {
		forestry.core.config.deprecated.Property property = Config.configOld.get(ident, category, def);
		property.comment = comment;
		booleanSettings.put(ident, Boolean.parseBoolean(property.value));
		Config.configOld.set(ident, category, booleanSettings.get(ident));
	}

	@Override
	public int getIntegerSetting(String ident) {
		if (integerSettings.containsKey(ident)) {
			return integerSettings.get(ident);
		}
		Proxies.log.warning("No such setting: " + ident);
		return -1;
	}

	@Override
	public float getFloatSetting(String ident) {
		if (floatSettings.containsKey(ident)) {
			return floatSettings.get(ident);
		}
		Proxies.log.warning("No such setting: " + ident);
		return 1;
	}

	@Override
	public boolean getBooleanSetting(String ident) {
		if (booleanSettings.containsKey(ident)) {
			return booleanSettings.get(ident);
		}
		Proxies.log.warning("No such setting: " + ident);
		return false;
	}

	@Override
	public ItemStack getStackSetting(String ident) {
		if (stackSettings.containsKey(ident)) {
			return stackSettings.get(ident);
		}
		Proxies.log.warning("No such setting: " + ident);
		return new ItemStack(Items.apple, 1);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

}
