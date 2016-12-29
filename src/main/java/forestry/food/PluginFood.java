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
package forestry.food;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import forestry.api.core.ForestryAPI;
import forestry.api.food.BeverageManager;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.config.Constants;
import forestry.core.recipes.RecipeUtil;
import forestry.food.items.ItemRegistryFood;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.item.ItemStack;

@ForestryPlugin(pluginID = ForestryPluginUids.FOOD, name = "Food", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.food.description")
public class PluginFood extends BlankForestryPlugin {
	@Nullable
	private static ItemRegistryFood items;

	public static ItemRegistryFood getItems() {
		Preconditions.checkState(items != null);
		return items;
	}

	@Override
	public void setupAPI() {
		super.setupAPI();

		// Init seasoner
		BeverageManager.infuserManager = new InfuserMixtureManager();
		BeverageManager.ingredientManager = new InfuserIngredientManager();
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryFood();
	}

	@Override
	public void preInit() {
//		LiquidRegistryHelper.registerLiquidContainer(Fluids.SHORT_MEAD, Constants.BUCKET_VOLUME, items.beverage.get(EnumBeverage.MEAD_SHORT, 1), new ItemStack(Items.GLASS_BOTTLE));

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = PluginApiculture.getItems();
			ItemStack normalPollenCluster = beeItems.pollenCluster.get(EnumPollenCluster.NORMAL, 1);
			ItemStack crystallinePollenCluster = beeItems.pollenCluster.get(EnumPollenCluster.CRYSTALLINE, 1);

			BeverageManager.ingredientManager.addIngredient(normalPollenCluster, "Strong Curative");
			BeverageManager.ingredientManager.addIngredient(crystallinePollenCluster, "Weak Curative");
			BeverageManager.infuserManager.addMixture(1, normalPollenCluster, BeverageEffects.strongAntidote);
			BeverageManager.infuserManager.addMixture(1, crystallinePollenCluster, BeverageEffects.weakAntidote);
		}
	}

	@Override
	public void registerRecipes() {
		// INFUSER
		RecipeUtil.addRecipe(getItems().infuser.getItemStack(),
				"X", "#", "X",
				'#', "ingotIron",
				'X', "ingotBronze");
	}
}
