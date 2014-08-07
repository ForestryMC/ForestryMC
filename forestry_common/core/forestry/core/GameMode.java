/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core;

import java.util.HashMap;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IGameMode;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.proxy.Proxies;

public class GameMode implements IGameMode {

	public static IGameMode getGameMode() {

		if (ForestryAPI.activeMode == null)
			ForestryAPI.activeMode = new GameMode(Config.gameMode);

		return ForestryAPI.activeMode;
	}

	private String identifier = "EASY";
	private final String category;

	private final HashMap<String, Boolean> booleanSettings = new HashMap<String, Boolean>();
	private final HashMap<String, Integer> integerSettings = new HashMap<String, Integer>();
	private final HashMap<String, Float> floatSettings = new HashMap<String, Float>();
	private final HashMap<String, ItemStack> stackSettings = new HashMap<String, ItemStack>();

	private static final float ENERGY_DEMAND_MODIFIER = 1.0f;
	private static final float FUEL_MODIFIER = 1.0f;

	private static final int FARM_FERTILIZER_VALUE = 2000;

	private final ItemStack recipeFertilizerOutputApatite = ForestryItem.fertilizerCompound.getItemStack(8);
	private final ItemStack recipeFertilizerOutputAsh = ForestryItem.fertilizerCompound.getItemStack(16);
	private final ItemStack recipeCompostOutputWheat = ForestryItem.fertilizerBio.getItemStack(4);
	private final ItemStack recipeCompostOutputAsh = ForestryItem.fertilizerBio.getItemStack(1);

	private final ItemStack recipeHumusOutputFertilizer = new ItemStack(ForestryBlock.soil, 8, 0);
	private final ItemStack recipeHumusOutputCompost = new ItemStack(ForestryBlock.soil, 8, 0);

	private final ItemStack recipeBogEarthOutputBucket = new ItemStack(ForestryBlock.soil, 6, 1);
	private final ItemStack recipeBogEarthOutputCans = new ItemStack(ForestryBlock.soil, 8, 1);

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

		Configuration config = Config.config;

		initSetting("energy.demand.modifier", ENERGY_DEMAND_MODIFIER, -1, "modifies the energy required to activate machines, as well as the max amount of energy stored and accepted.");
		initSetting("farms.fertilizer.value", FARM_FERTILIZER_VALUE, -1, "modifies the time a piece of fertilizer lasts in a farm.");

		initSetting("fuel.ethanol.generator", FUEL_MODIFIER, -1, "modifies the energy provided by ethanol in a Bio Generator.");
		initSetting("fuel.ethanol.combustion", FUEL_MODIFIER, -1, "modifies the energy provided by ethanol in Buildcraft Combustion Engines.");
		initSetting("fuel.biomass.generator", FUEL_MODIFIER, -1, "modifies the energy provided by Biomass in a Bio Generator.");
		initSetting("fuel.biomass.biogas", FUEL_MODIFIER, -1, "modifies the energy provided by Biomass in Biogas Engines.");

		initSetting("recipe.output.fertilizer.apatite", recipeFertilizerOutputApatite, "amount of fertilizer yielded by the recipe using apatite.");
		initSetting("recipe.output.fertilizer.ash", recipeFertilizerOutputAsh, "amount of fertilizer yielded by the recipe using ash.");
		initSetting("recipe.output.compost.wheat", recipeCompostOutputWheat, "amount of compost yielded by the recipe using wheat.");
		initSetting("recipe.output.compost.ash", recipeCompostOutputAsh,  "amount of compost yielded by the recipe using ash.");
		initSetting("recipe.output.humus.fertilizer", recipeHumusOutputFertilizer, "amount of humus yielded by the recipe using fertilizer.");
		initSetting("recipe.output.humus.compost", recipeHumusOutputCompost, "amount of humus yielded by the recipe using compost.");
		initSetting("recipe.output.bogearth.bucket", recipeBogEarthOutputBucket, "amount of bog earth yielded by the recipe using buckets.");
		initSetting("recipe.output.bogearth.can", recipeBogEarthOutputCans, "amount of bog earth yielded by the recipes using cans, cells or capsules.");

		initSetting("recipe.output.can", recipeCanOutput, "amount yielded by the recipe for tin cans.");
		initSetting("recipe.output.capsule", recipeCapsuleOutput, "amount yielded by the recipe for wax capsules.");
		initSetting("recipe.output.refractory", recipeRefractoryOutput, "amount yielded by the recipe for refractory capsules.");

		initSetting("fermenter.cycles.fertilizer", FERMENTATION_DURATION_FERTILIZER, -1, "modifies the amount of cycles fertilizer can keep a fermenter going.");
		initSetting("fermenter.cycles.compost", FERMENTATION_DURATION_COMPOST, -1, "modifies the amount of cycles compost can keep a fermenter going.");

		initSetting("fermenter.value.fertilizer", FERMENTED_CYCLE_FERTILIZER, -1, "modifies the amount of biomass per cycle a fermenter will produce using fertilizer.");
		initSetting("fermenter.value.compost", FERMENTED_CYCLE_COMPOST, -1, "modifies the amount of biomass per cycle a fermenter will produce using compost.");

		initSetting("fermenter.yield.sapling", FERMENTED_SAPLING, FERMENTED_SAPLING * 8, "modifies the base amount of biomass a sapling will yield in a fermenter, affected by sappiness trait.");
		initSetting("fermenter.yield.cactus", FERMENTED_CACTI, FERMENTED_CACTI * 8, "modifies the amount of biomass a piece of cactus will yield in a fermenter.");
		initSetting("fermenter.yield.wheat", FERMENTED_WHEAT, FERMENTED_WHEAT * 8, "modifies the amount of biomass a piece of wheat will yield in a fermenter.");
		initSetting("fermenter.yield.cane", FERMENTED_CANE, FERMENTED_CANE * 8, "modifies the amount of biomass a piece of sugar cane will yield in a fermenter.");
		initSetting("fermenter.yield.mushroom", FERMENTED_MUSHROOM, FERMENTED_MUSHROOM * 8, "modifies the amount of biomass a mushroom will yield in a fermenter.");

		initSetting("squeezer.liquid.seed", SQUEEZED_LIQUID_SEED, SQUEEZED_LIQUID_SEED * 8, "modifies the amount of seed oil squeezed from a single seed. other sources are based off this.");
		initSetting("squeezer.liquid.apple", SQUEEZED_LIQUID_APPLE, SQUEEZED_LIQUID_APPLE * 8, "modifies the amount of juice squeezed from a single apple. other sources are based off this.");
		initSetting("squeezer.mulch.apple", SQUEEZED_MULCH_APPLE, SQUEEZED_MULCH_APPLE * 8, "modifies the chance of mulch per squeezed apple.");
		initSetting("energy.engine.clockwork", true, "set to false to disable the clockwork engine.");

		config.save();

	}

	private void initSetting(String ident, ItemStack def, String comment) {
		Property property = Config.config.get(ident, category, def.stackSize);
		property.Comment = comment;
		ItemStack changed = def.copy();
		changed.stackSize = Integer.parseInt(property.Value);
		stackSettings.put(ident, changed);
	}

	private void initSetting(String ident, int def, int max, String comment) {
		Property property = Config.config.get(ident, category, def);
		if(max < 0) {
			property.Comment = comment;
			integerSettings.put(ident, Integer.parseInt(property.Value));
		} else {
			property.Comment = comment + " (max: " + max + ")";
			integerSettings.put(ident, Math.min(Integer.parseInt(property.Value), max));
		}
		Config.config.set(ident, category, integerSettings.get(ident));

	}

	private void initSetting(String ident, float def, float max, String comment) {
		Property property = Config.config.get(ident, category, def);
		if(max < 0) {
			property.Comment = comment;
			floatSettings.put(ident, Float.parseFloat(property.Value));
		} else {
			property.Comment = comment + " (max: " + max + ")";
			floatSettings.put(ident, Math.min(Float.parseFloat(property.Value), max));
		}
		Config.config.set(ident, category, floatSettings.get(ident));
	}

	private void initSetting(String ident, boolean def, String comment) {
		Property property = Config.config.get(ident, category, def);
		property.Comment = comment;
		booleanSettings.put(ident, Boolean.parseBoolean(property.Value));
		Config.config.set(ident, category, booleanSettings.get(ident));
	}

	@Override
	public int getIntegerSetting(String ident) {
		if(integerSettings.containsKey(ident))
			return integerSettings.get(ident);
		Proxies.log.warning("No such setting: " + ident);
		return -1;
	}

	@Override
	public float getFloatSetting(String ident) {
		if(floatSettings.containsKey(ident))
			return floatSettings.get(ident);
		Proxies.log.warning("No such setting: " + ident);
		return 1;
	}

	@Override
	public boolean getBooleanSetting(String ident) {
		if(booleanSettings.containsKey(ident))
			return booleanSettings.get(ident);
		Proxies.log.warning("No such setting: " + ident);
		return false;
	}

	@Override
	public ItemStack getStackSetting(String ident) {
		if(stackSettings.containsKey(ident))
			return stackSettings.get(ident);
		Proxies.log.warning("No such setting: " + ident);
		return new ItemStack(Items.apple, 1);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

}
