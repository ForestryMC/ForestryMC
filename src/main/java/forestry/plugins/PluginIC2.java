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
package forestry.plugins;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;

import ic2.api.item.IC2Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;

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

@Plugin(pluginID = "IC2", name = "IndustrialCraft2", author = "SirSengir", url = Defaults.URL, description = "Compatibility plugin for IC2.")
public class PluginIC2 extends ForestryPlugin {

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
	public static ItemStack emptyCell;
	public static ItemStack lavaCell;
	public static ItemStack waterCell;
	public static ItemStack rubber;
	public static ItemStack scrap;
	public static ItemStack silver;
	public static ItemStack brass;
	public static ItemStack uuMatter;

	public PluginIC2() {
		if (PluginIC2.instance == null)
			PluginIC2.instance = this;
	}

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded("IC2");
	}

	@Override
	public String getFailMessage() {
		return "IndustrialCraft2 not found";
	}

	@Override
	public void doInit() {
		config = Config.config;

		initLiquidContainers();
		initRubberChain();
		initFermentation();

		// Remove some items from the recycler
		Recipes.recyclerBlacklist.add(new RecipeInputItemStack(ForestryItem.beeQueenGE.getItemStack()));
		Recipes.recyclerBlacklist.add(new RecipeInputItemStack(ForestryItem.beePrincessGE.getItemStack()));
	}

	@Override
	protected void registerBackpackItems() {
		if (BackpackManager.backpackItems == null)
			return;

		if (resin != null)
			BackpackManager.definitions.get("forester").addValidItem(resin);
		if (rubber != null)
			BackpackManager.definitions.get("forester").addValidItem(rubber);
	}

	@Override
	protected void registerCrates() {

		// Add crates for the IC2 items that exist.
		resin = IC2Items.getItem("resin");
		if (resin != null) {
			ForestryItem.cratedResin.registerItem(new ItemCrated(), "cratedResin");
			((ItemCrated) ForestryItem.cratedResin.item()).setContained(ForestryItem.cratedResin.getItemStack(), resin);
		}

		rubber = IC2Items.getItem("rubber");
		if (rubber != null) {
			ForestryItem.cratedRubber.registerItem(new ItemCrated(), "cratedRubber");
			((ItemCrated) ForestryItem.cratedRubber.item()).setContained(ForestryItem.cratedRubber.getItemStack(), rubber);
		}

		scrap = IC2Items.getItem("scrap");
		if (scrap != null) {
			ForestryItem.cratedScrap.registerItem(new ItemCrated(), "cratedScrap");
			((ItemCrated) ForestryItem.cratedScrap.item()).setContained(ForestryItem.cratedScrap.getItemStack(), scrap);
		}

		uuMatter = IC2Items.getItem("matter");
		if (uuMatter != null) {
			ForestryItem.cratedUUM.registerItem(new ItemCrated(), "cratedUUM");
			((ItemCrated) ForestryItem.cratedUUM.item()).setContained(ForestryItem.cratedUUM.getItemStack(), uuMatter);
		}

		silver = IC2Items.getItem("silverIngot");
		if (silver != null) {
			ForestryItem.cratedSilver.registerItem(new ItemCrated(), "cratedSilver");
			((ItemCrated) ForestryItem.cratedSilver.item()).setContained(ForestryItem.cratedSilver.getItemStack(), silver);
		}

		brass = IC2Items.getItem("bronzeIngot");
		if (brass != null) {
			ForestryItem.cratedBrass.registerItem(new ItemCrated(), "cratedBrass");
			((ItemCrated) ForestryItem.cratedBrass.item()).setContained(ForestryItem.cratedBrass.getItemStack(), brass);
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
		if (emptyCell == null) {
			Proxies.log.fine("IC2 empty cell could not be found. Skipped adding IC2 liquid containers.");
			return;
		}

		lavaCell = IC2Items.getItem("lavaCell");
		if (lavaCell != null)
			LiquidHelper.injectTinContainer(Defaults.LIQUID_LAVA, Defaults.BUCKET_VOLUME, lavaCell, emptyCell);

		waterCell = IC2Items.getItem("waterCell");
		if (waterCell != null) {
			LiquidHelper.injectTinContainer(Defaults.LIQUID_WATER, Defaults.BUCKET_VOLUME, waterCell, emptyCell);

			ItemStack bogEarthCan = GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can");
			if (bogEarthCan.stackSize > 0)
				Proxies.common.addRecipe(bogEarthCan, "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', waterCell, 'Y', Blocks.sand);
		}
	}

	private void initRubberChain() {
		treetap = IC2Items.getItem("treetap");
		rubberwood = IC2Items.getItem("rubberWood");

		resin = IC2Items.getItem("resin");
		if (resin != null)
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.propolis.getItemStack(), resin);
		else
			Proxies.log.fine("Missing IC2 resin, skipping centrifuge recipe for propolis to resin.");

		rubbersapling = IC2Items.getItem("rubberSapling");
		if (rubbersapling != null) {
			RecipeUtil.injectLeveledRecipe(rubbersapling, GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling"), Defaults.LIQUID_BIOMASS);
			BackpackManager.backpackItems[2].add(rubbersapling);
		} else
			Proxies.log.fine("Missing IC2 rubber sapling, skipping fermenter recipe for converting rubber sapling to biomass.");

		if (rubbersapling != null && resin != null) {
			String saplingName = GameData.getBlockRegistry().getNameForObject(StackUtils.getBlock(rubbersapling));
			String resinName = GameData.getItemRegistry().getNameForObject(resin.getItem());
			String imc = String.format("farmArboreal@%s.%s.%s.%s",
					saplingName, rubbersapling.getItemDamage(),
					resinName, resin.getItemDamage());
			Proxies.log.finest("Sending IMC '%s'.", imc);
			FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", imc);
		}

		rubberleaves = IC2Items.getItem("rubberLeaves");
		if (rubberleaves != null)
			BackpackManager.backpackItems[2].add(rubberleaves);
		else
			Proxies.log.fine("Missing IC2 rubber leaves");

		// Rubber wood is added via ore dictionary.
	}

}
