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

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.FarmableBasicAgricraft;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginManager;

@Plugin(pluginID = "AgriCraft", name = "AgriCraft", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.agricraft.description")
public class PluginAgriCraft extends ForestryPlugin {

	private static final String AgriCraft = "AgriCraft";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(AgriCraft);
	}

	@Override
	public String getFailMessage() {
		return "AgriCraft not found";
	}

	@Override
	protected void registerRecipes() {

		ImmutableList<String> seeds = ImmutableList.of(
				"Allium", //Vanilla
				"Dandelion",
				"Daisy",
				"TulipRed",
				"TulipPink",
				"TulipOrange",
				"TulipWhite",
				"Sugarcane",
				"Cactus",
				"Carrot",
				"Potato",
				"Poppy",
				"Orchid",
				"ShroomRed",
				"ShroomBrown",

				"BotaniaCyan", //Botania
				"BotaniaLime",
				"BotaniaRed",
				"BotaniaLightGray",
				"BotaniaOrange",
				"BotaniaBlack",
				"BotaniaLightBlue",
				"BotaniaPink",
				"BotaniaWhite",
				"BotaniaGreen",
				"BotaniaYellow",
				"BotaniaMagenta",
				"BotaniaBrown",
				"BotaniaPurple",
				"BotaniaBlue",
				"BotaniaGray"


		);
		int seedamount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		for (String seedName : seeds) {
			ItemStack seed = GameRegistry.findItemStack(AgriCraft, "seed" + seedName, 1);
			if (seed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{seed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
		}

		Block cropBlock = GameRegistry.findBlock(AgriCraft, "crops");
		if (cropBlock != null && PluginManager.Module.FARMING.isEnabled()) {
			Farmables.farmables.get("farmOrchard").add(new FarmableBasicAgricraft(cropBlock, 7));
		}
	}

}

