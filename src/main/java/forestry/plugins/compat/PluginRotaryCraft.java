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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.FarmableBasicFruit;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginManager;

@Plugin(pluginID = "RotaryCraft", name = "RotaryCraft", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.rotarycraft.description")
public class PluginRotaryCraft extends ForestryPlugin {

	private static final String RC = "RotaryCraft";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(RC);
	}

	@Override
	public String getFailMessage() {
		return "RotaryCraft not found";
	}

	@Override
	protected void registerRecipes() {

		ItemStack canolaSeed = GameRegistry.findItemStack(RC, "rotarycraft_item_canola", 1);
		Block canolaCrop = GameRegistry.findBlock(RC, "rotarycraft_block_canola");

		int seedAmount = (ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed") / 16);
		seedAmount = Math.max(seedAmount, 1); // Produce at least 1 mb.
		if (canolaSeed != null && canolaCrop != null) {
			if(PluginManager.Module.FACTORY.isEnabled()) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{canolaSeed}, Fluids.SEEDOIL.getFluid(seedAmount));
			}
			if(PluginManager.Module.FARMING.isEnabled()) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(canolaSeed, canolaCrop, 9));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(canolaCrop, 9));
			}
		}
	}

}
