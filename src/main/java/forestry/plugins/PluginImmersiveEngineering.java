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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.farming.logic.FarmableGenericCrop;

@Plugin(pluginID = "ImmersiveEngineering", name = "ImmersiveEngineering", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.immersiveengineering.description")
public class PluginImmersiveEngineering extends ForestryPlugin {

	private static final String ImEng = "ImmersiveEngineering";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(ImEng);
	}

	@Override
	public String getFailMessage() {
		return "Immersive Engineering not found";
	}

	@Override
	protected void registerRecipes() {

		ItemStack hempSeed = new ItemStack(GameRegistry.findItem(ImEng, "seed"), 1);
		Block hempCrop = GameRegistry.findBlock(ImEng, "hemp");
		ItemStack hempFiber = new ItemStack(GameRegistry.findItem(ImEng, "material"), 1, 3);
		int seedAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");
		if (hempSeed != null && hempCrop != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{hempSeed}, Fluids.SEEDOIL.getFluid(seedAmount));
			Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(hempSeed, hempCrop, 4, hempFiber));
		}
	}

}
