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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.api.food.BeverageManager;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.LiquidRegistryHelper;
import forestry.core.recipes.RecipeUtil;
import forestry.food.BeverageEffect;
import forestry.food.InfuserIngredientManager;
import forestry.food.InfuserMixtureManager;
import forestry.food.items.EnumBeverage;
import forestry.food.items.ItemRegistryFood;

@Plugin(pluginID = "Food", name = "Food", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.food.description")
public class PluginFood extends ForestryPlugin {

	public static ItemRegistryFood items;

	@Override
	protected void setupAPI() {
		super.setupAPI();

		// Init seasoner
		BeverageManager.infuserManager = new InfuserMixtureManager();
		BeverageManager.ingredientManager = new InfuserIngredientManager();
	}

	@Override
	protected void registerItemsAndBlocks() {
		items = new ItemRegistryFood();
	}

	@Override
	protected void preInit() {
		super.preInit();

		LiquidRegistryHelper.registerLiquidContainer(Fluids.SHORT_MEAD, Constants.BUCKET_VOLUME, items.beverage.get(EnumBeverage.MEAD_SHORT, 1), new ItemStack(Items.glass_bottle));
		LiquidRegistryHelper.registerLiquidContainer(Fluids.MEAD, Constants.BUCKET_VOLUME, items.beverage.get(EnumBeverage.MEAD, 1), new ItemStack(Items.glass_bottle));

		ItemRegistryApiculture beeItems = PluginApiculture.items;
		if (beeItems != null) {
			ItemStack normalPollenCluster = beeItems.pollenCluster.get(EnumPollenCluster.NORMAL, 1);
			ItemStack crystallinePollenCluster = beeItems.pollenCluster.get(EnumPollenCluster.CRYSTALLINE, 1);

			BeverageManager.ingredientManager.addIngredient(normalPollenCluster, "Strong Curative");
			BeverageManager.ingredientManager.addIngredient(crystallinePollenCluster, "Weak Curative");
			BeverageManager.infuserManager.addMixture(1, normalPollenCluster, BeverageEffect.strongAntidote);
			BeverageManager.infuserManager.addMixture(1, crystallinePollenCluster, BeverageEffect.weakAntidote);
		}
	}

	@Override
	protected void registerRecipes() {
		// INFUSER
		RecipeUtil.addRecipe(items.infuser.getItemStack(),
				"X", "#", "X",
				'#', "ingotIron",
				'X', "ingotBronze");
	}
}
