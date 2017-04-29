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

import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@ForestryPlugin(pluginID = ForestryPluginUids.BUILDCRAFT_TRANSPORT, name = "BuildCraft 6 Transport", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
public class PluginBuildCraftTransport extends BlankForestryPlugin {

	private static final String BCT = "buildcrafttransport";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(BCT);
	}

	@Override
	public String getFailMessage() {
		return "buildcrafttransport not found";
	}

	@Override
	public void registerRecipes() {
		Item beeswax = PluginCore.getItems().beeswax;
		Item pipeWaterproof = ForgeRegistries.ITEMS.getValue(new ResourceLocation(BCT, "waterproof"));
		if (pipeWaterproof != null) {
			RecipeUtil.addShapelessRecipe(new ItemStack(pipeWaterproof), beeswax);
		} else {
			Log.warning("No BuildCraft pipe waterproof found.");
		}
	}
}
