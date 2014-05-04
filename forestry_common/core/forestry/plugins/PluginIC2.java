/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.plugins;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;

import ic2.api.item.IC2Items;

import forestry.api.core.IPlugin;
import forestry.api.core.PluginInfo;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.items.ItemCrated;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.RecipeUtil;
import forestry.core.utils.StackUtils;

@PluginInfo(pluginID = "IC2", name = "IndustrialCraft2", author = "SirSengir", url = Defaults.URL, description = "Compatibility plugin for IC2.")
public class PluginIC2 implements IPlugin {

	public static PluginIC2 instance;
	public static Configuration config;
	// Ignore IC2?
	public static boolean ignore;
	// IC2 stuff
	public static ItemStack plantBall;
	public static ItemStack compressedPlantBall;
	public static ItemStack wrench;
	public static ItemStack treetap;
	public static ItemStack resin;
	public static ItemStack rubbersapling;
	public static ItemStack rubberwood;
	public static ItemStack rubberleaves;
	public static ItemStack fuelcanFilled;
	public static ItemStack fuelcanEmpty;
	public static ItemStack emptyCell;
	public static ItemStack lavaCell;
	public static ItemStack waterCell;
	public static ItemStack rubber;
	public static ItemStack scrap;
	public static int fuelcanMeta;

	public PluginIC2() {
		if (PluginIC2.instance == null)
			PluginIC2.instance = this;
	}

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded("IC2");
	}

	@Override
	public void doInit() {
		config = Config.config;

		if (!isAvailable()) return;

		initLiquidContainers();
		initRubberChain();
		initFermentation();
		initCrates();

		//Ic2Recipes.addRecyclerBlacklistItem(ForestryItem.beeQueenGE);
		//Ic2Recipes.addRecyclerBlacklistItem(ForestryItem.beePrincessGE);

		// Remove some items from the recycler
		registerBackpackItems();

		if (rubbersapling != null && resin != null) {
			String imc = String.format("farmArboreal@%s.%s.%s.%s",
					GameData.blockRegistry.getNameForObject(StackUtils.getBlock(rubbersapling)),
					rubbersapling.getItemDamage(),
					GameData.itemRegistry.getNameForObject(resin.getItem()),
					resin.getItemDamage());
			Proxies.log.finest("Sending IMC '%s'.", imc);
			FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", imc);
		}
	}

	private void initFermentation() {
		plantBall = IC2Items.getItem("plantBall");
		compressedPlantBall = IC2Items.getItem("compressedPlantBall");
		if (plantBall == null || compressedPlantBall == null) {
			Proxies.log.fine("No IC2 plantballs found.");
			return;
		}

		// Add extra recipes
		RecipeUtil.injectLeveledRecipe(plantBall, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat") * 4, Defaults.LIQUID_BIOMASS);
		RecipeUtil.injectLeveledRecipe(compressedPlantBall, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat") * 5, Defaults.LIQUID_BIOMASS);
	}

	private void initLiquidContainers() {
		emptyCell = IC2Items.getItem("cell");
		lavaCell = IC2Items.getItem("lavaCell");
		waterCell = IC2Items.getItem("waterCell");
		if (emptyCell == null || lavaCell == null || waterCell == null) {
			Proxies.log.fine("Any of the following IC2 items could not be found: empty cell, water cell, lava cell. Skipped adding IC2 liquid containers.");
			return;
		}

		LiquidHelper.injectTinContainer(Defaults.LIQUID_LAVA, Defaults.BUCKET_VOLUME, lavaCell, emptyCell);
		LiquidHelper.injectTinContainer(Defaults.LIQUID_WATER, Defaults.BUCKET_VOLUME, waterCell, emptyCell);
		if (GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can").stackSize > 0)
			Proxies.common
			.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can"),
					new Object[] { "#Y#", "YXY", "#Y#", Character.valueOf('#'), Blocks.dirt, Character.valueOf('X'), waterCell, Character.valueOf('Y'),
				Blocks.sand });
	}

	private void initRubberChain() {
		treetap = IC2Items.getItem("treetap");
		resin = IC2Items.getItem("resin");
		rubberwood = IC2Items.getItem("rubberWood");
		rubbersapling = IC2Items.getItem("rubberSapling");
		rubberleaves = IC2Items.getItem("rubberLeaves");
		fuelcanFilled = IC2Items.getItem("filledFuelCan");
		fuelcanEmpty = IC2Items.getItem("fuelCan");
		if (treetap == null || resin == null || rubberwood == null || rubbersapling == null || rubberleaves == null || fuelcanFilled == null
				|| fuelcanEmpty == null) {
			Proxies.log
			.fine("Any of the following IC2 blocks and items could not be found: resin, rubber wood, saplings or leaves, filled fuel cans, empty fuel cans. Skipped adding rubber chain.");
			return;
		}

		// Add extra recipes
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.propolis.getItemStack(), resin);
		ItemStack fuelcanStack = new ItemStack(fuelcanFilled.getItem(), 1, 0);
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("value", 15288);
		fuelcanStack.setTagCompound(compound);
		RecipeManagers.bottlerManager.addRecipe(20, LiquidHelper.getLiquid(Defaults.LIQUID_ETHANOL, Defaults.BOTTLER_FUELCAN_VOLUME), fuelcanEmpty, fuelcanStack);

		RecipeUtil.injectLeveledRecipe(rubbersapling, GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling"), Defaults.LIQUID_BIOMASS);

		// Add backpack items
		BackpackManager.backpackItems[2].add(rubbersapling);
		BackpackManager.backpackItems[2].add(rubberleaves);
		// Rubber wood is added via ore dictionary.
	}

	private void initCrates() {
		resin = IC2Items.getItem("resin");
		rubber = IC2Items.getItem("rubber");
		scrap = IC2Items.getItem("scrap");

		// Add crates for the IC2 items that exist.
		if (resin != null) {
			((ItemCrated) ForestryItem.cratedResin.item()).setContained(ForestryItem.cratedResin.getItemStack(), resin);
		}

		if (rubber != null) {
			((ItemCrated) ForestryItem.cratedRubber.item()).setContained(ForestryItem.cratedRubber.getItemStack(), rubber);
		}

		if (scrap != null) {
			((ItemCrated) ForestryItem.cratedScrap.item()).setContained(ForestryItem.cratedScrap.getItemStack(), scrap);
		}
	}

	private void registerBackpackItems() {
		if (BackpackManager.backpackItems == null)
			return;

		if (resin != null)
			BackpackManager.definitions.get("forester").addValidItem(resin);
		if (rubber != null)
			BackpackManager.definitions.get("forester").addValidItem(rubber);
	}

	@Override
	public void preInit() {
	}

	@Override
	public void postInit() {
	}
}
