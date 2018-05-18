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

import buildcraft.api.recipes.BuildcraftRecipeRegistry;
import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;


@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.BUILDCRAFT_RECIPES, name = "BuildCraft 6 Recipes", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.buildcraft6.description")
public class PluginBuildCraftRecipes extends BlankForestryModule {

	@Override
	public boolean isAvailable() {
		return ModUtil.isAPILoaded("buildcraft.api.recipes", "[2.0, 4.0)");
	}

	@Override
	public String getFailMessage() {
		return "Compatible BuildCraftAPI|recipes version not found";
	}

	@Optional.Method(modid = "BuildCraftAPI|recipes")
	@Override
	public void registerRecipes() {
		// Add recipe for ethanol
		// TODO therealfarfetchd
		// BuildcraftRecipeRegistry.refinery.addRecipe("forestry:BiomassToEthanol", Fluids.BIOMASS.getFluid(4), Fluids.BIO_ETHANOL.getFluid(1), 100, 1);
		BuildcraftRecipeRegistry.refineryRecipes.addDistillationRecipe(Fluids.BIOMASS.getFluid(4), null, Fluids.BIO_ETHANOL.getFluid(1), 100);
	}
}
