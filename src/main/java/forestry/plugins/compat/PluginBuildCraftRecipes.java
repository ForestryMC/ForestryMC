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

import forestry.core.config.Constants;
import forestry.core.utils.ModUtil;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraftforge.fml.common.Optional;


@ForestryPlugin(pluginID = ForestryPluginUids.BUILDCRAFT_RECIPES, name = "BuildCraft 6 Recipes", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
public class PluginBuildCraftRecipes extends BlankForestryPlugin {

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
		// TODO: Buildcraft for 1.9
//		BuildcraftRecipeRegistry.refinery.addRecipe("forestry:BiomassToEthanol", Fluids.BIOMASS.getFluid(4), Fluids.BIO_ETHANOL.getFluid(1), 100, 1);
	}
}
