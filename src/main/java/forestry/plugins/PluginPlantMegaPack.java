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

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.farming.Farmables;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.farming.logic.FarmableStacked;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

@Plugin(pluginID = "PlantMegaPack", name = "PlantMegaPack", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.plantmegapack.description")
public class PluginPlantMegaPack extends ForestryPlugin {

	private static final String PlantMP = "plantmegapack";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(PlantMP);
	}

	@Override
	public String getFailMessage() {
		return "Plant Mega Pack not found";
	}

	@Override
	protected void registerRecipes() {

		ImmutableList<String> reedLikePlant = ImmutableList.of(
				"bambooAsper",
				"bambooFargesiaRobusta",
				"bambooGiantTimber",
				"bambooGolden",
				"bambooMoso",
				"bambooShortTassled",
				"bambooTimorBlack",
				"bambooTropicalBlue",
				"bambooWetForest"
		);

		ImmutableList<String> waterPlant = ImmutableList.of(
				"waterKelpGiantGRN",
				"waterKelpGiantYEL"
		);

		for (String reedLike : reedLikePlant) {
			Block reedBlock = GameRegistry.findBlock(PlantMP, reedLike);
			ItemStack reedStack = GameRegistry.findItemStack(PlantMP,reedLike,1);
			if (reedBlock != null && reedStack != null) {
				RecipeUtil.injectLeveledRecipe(reedStack, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
				Farmables.farmables.get("farmPoales").add(new FarmableStacked(reedBlock, 14,4));
			}
		}
		for (String wPlant : waterPlant) {
			ItemStack waterPlantStack = GameRegistry.findItemStack(PlantMP,wPlant,1);
			if (waterPlantStack != null) {
				RecipeUtil.injectLeveledRecipe(waterPlantStack, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			}
		}
	}
}
