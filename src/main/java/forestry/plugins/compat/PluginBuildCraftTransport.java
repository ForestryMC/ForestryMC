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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.core.config.Constants;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginCore;

@Plugin(pluginID = "BC6|Transport", name = "BuildCraft 6 Transport", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
public class PluginBuildCraftTransport extends ForestryPlugin {

	private static final String BCT = "BuildCraft|Transport";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(BCT);
	}

	@Override
	public String getFailMessage() {
		return "BuildCraft|Transport not found";
	}

	@Override
	protected void registerRecipes() {
		Item beeswax = PluginCore.items.beeswax;
		Item pipeWaterproof = GameRegistry.findItem(BCT, "pipeWaterproof");
		if (pipeWaterproof != null) {
			RecipeUtil.addShapelessRecipe(new ItemStack(pipeWaterproof), beeswax);
		} else {
			Log.fine("No BuildCraft pipe waterproof found.");
		}
	}
}
