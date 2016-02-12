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
package forestry.plugins.compat;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.FarmableBasicGrowthCraft;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginApiculture;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginManager;

@Plugin(pluginID = "Growthcraft", name = "Growthcraft", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.growthcraft.description")
public class PluginGrowthCraft extends ForestryPlugin {

	private static final String GC = "Growthcraft";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(GC, "[1.7.10-1.0.0,1.7.10-2.5.0)");
	}

	@Override
	public String getFailMessage() {
		return "GrowthCraft not found";
	}

	@Override
	protected void registerRecipes() {

		int saplingYield = ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling");
		int juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		int seedamount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");

		ItemStack appleSeed = GameRegistry.findItemStack("Growthcraft|Apples", "grc.appleSeeds", 1);
		ItemStack grapeSeed = GameRegistry.findItemStack("Growthcraft|Grapes", "grc.grapeSeeds", 1);
		ItemStack hopSeed = GameRegistry.findItemStack("Growthcraft|Hops", "grc.hopSeeds", 1);
		ItemStack riceSeed = GameRegistry.findItemStack("Growthcraft|Rice", "grc.rice", 1);

		if (appleSeed != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{appleSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			BackpackManager.backpackItems[2].add(appleSeed);
		}
		if (grapeSeed != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{grapeSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			BackpackManager.backpackItems[2].add(grapeSeed);
		}
		if (hopSeed != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{hopSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			BackpackManager.backpackItems[2].add(hopSeed);
		}
		if (riceSeed != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{riceSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			BackpackManager.backpackItems[2].add(riceSeed);
		}

		ItemStack hops = GameRegistry.findItemStack("Growthcraft|Hops", "grc.hops", 1);

		if (hops != null) {
			RecipeUtil.addFermenterRecipes(hops, saplingYield, Fluids.BIOMASS);
			BackpackManager.backpackItems[2].add(hops);

		}
		Block hopVine = GameRegistry.findBlock("Growthcraft|Hops", "grc.hopVine");
		if (hopVine != null) {
			Farmables.farmables.get("farmOrchard").add(new FarmableBasicGrowthCraft(hopVine, 3, false, false)); // need custom fruit to weed out rope
		}
		Block riceBlock = GameRegistry.findBlock("Growthcraft|Rice", "grc.riceBlock");
		if (riceBlock != null) {
			Farmables.farmables.get("farmOrchard").add(new FarmableBasicGrowthCraft(riceBlock, 7, true, false)); //need to set paddyfield to 7
		}

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			Item comb = GameRegistry.findItem("Growthcraft|Bees", "grc.honeyComb");
			if (comb != null) {
				ItemStack emptyComb = new ItemStack(comb, 1, 0);
				RecipeManagers.centrifugeManager.addRecipe(20, emptyComb, ImmutableMap.of(
								PluginCore.items.beeswax.getItemStack(), 1.0f)
				);
				ItemStack fullComb = new ItemStack(comb, 1, 1);
				RecipeManagers.centrifugeManager.addRecipe(20, fullComb, ImmutableMap.of(
								PluginCore.items.beeswax.getItemStack(), 1.0f,
								PluginApiculture.items.honeyDrop.getItemStack(), 0.9f,
								PluginApiculture.items.honeydew.getItemStack(), 0.1f)
				);
			}
		}

		ItemStack bamboo = GameRegistry.findItemStack("Growthcraft|Bamboo", "grc.bamboo", 1);
		ItemStack bambooShoot = GameRegistry.findItemStack("Growthcraft|Bamboo", "grc.bambooShoot", 1);
		ItemStack bambooShootFood = GameRegistry.findItemStack("Growthcraft|Bamboo", "grc.bambooShootFood", 1);
		if (bamboo != null) {
			RecipeUtil.addFermenterRecipes(bamboo, saplingYield, Fluids.BIOMASS);
			BackpackManager.backpackItems[2].add(bamboo);
		}
		if (bambooShoot != null) {
			RecipeUtil.addFermenterRecipes(bambooShoot, saplingYield, Fluids.BIOMASS);
			BackpackManager.backpackItems[2].add(bambooShoot);
		}
		if (bambooShootFood != null) {
			RecipeUtil.addFermenterRecipes(bambooShootFood, saplingYield, Fluids.BIOMASS);
			BackpackManager.backpackItems[2].add(bambooShootFood);
		}

		ItemStack grapes = GameRegistry.findItemStack("Growthcraft|Grapes", "grc.grapes", 1); //squeeze

		if (grapes.getItem() != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{grapes}, Fluids.JUICE.getFluid(juiceAmount));
			BackpackManager.backpackItems[2].add(grapes);
		}
		Block grapeBlock = GameRegistry.findBlock("Growthcraft|Grapes", "grc.grapeBlock");
		if (grapeBlock != null) {
			Farmables.farmables.get("farmOrchard").add(new FarmableBasicGrowthCraft(grapeBlock, 0, false, true));
		}

		ItemStack bambooLeaves = GameRegistry.findItemStack("Growthcraft|Bamboo", "grc.bambooLeaves", 1);
		if (bambooLeaves != null) {
			BackpackManager.backpackItems[2].add(bambooLeaves);
		}

	}
}
