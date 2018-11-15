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

import net.minecraftforge.fml.common.Optional;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

import buildcraft.api.mj.MjAPI;
import buildcraft.api.recipes.BuildcraftRecipeRegistry;

@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.BUILDCRAFT_RECIPES, name = "BuildCraft 6 Recipes", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.buildcraft6.description")
public class PluginBuildCraftRecipes extends BlankForestryModule {

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(Constants.BCLIB_MOD_ID, "[7.99.17,8.0)");
	}

	@Override
	public String getFailMessage() {
		return "Compatible BuildCraftAPI|recipes version not found";
	}

	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	@Override
	public void registerRecipes() {
		// Add recipe for ethanol
		BuildcraftRecipeRegistry.refineryRecipes.addDistillationRecipe(Fluids.BIOMASS.getFluid(10),
			Fluids.BIO_ETHANOL.getFluid(0), Fluids.BIO_ETHANOL.getFluid(3), 20 * MjAPI.MJ);
	}
}
