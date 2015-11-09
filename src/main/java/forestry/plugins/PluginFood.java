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

import java.util.Collections;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.food.BeverageManager;
import forestry.core.GuiHandlerBase;
import forestry.core.config.Constants;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.LiquidRegistryHelper;
import forestry.core.items.ItemForestryFood;
import forestry.core.items.ItemWithGui;
import forestry.core.network.GuiId;
import forestry.core.recipes.RecipeUtil;
import forestry.food.BeverageEffect;
import forestry.food.GuiHandlerFood;
import forestry.food.InfuserIngredientManager;
import forestry.food.InfuserMixtureManager;
import forestry.food.items.ItemAmbrosia;
import forestry.food.items.ItemBeverage;
import forestry.food.items.ItemBeverage.BeverageInfo;

@Plugin(pluginID = "Food", name = "Food", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.food.description")
public class PluginFood extends ForestryPlugin {

	@Override
	protected void setupAPI() {
		super.setupAPI();

		// Init seasoner
		BeverageManager.infuserManager = new InfuserMixtureManager();
		BeverageManager.ingredientManager = new InfuserIngredientManager();
	}

	@Override
	protected void registerItemsAndBlocks() {
		// / FOOD ITEMS
		ForestryItem.honeyedSlice.registerItem(new ItemForestryFood(8, 0.6f), "honeyedSlice");
		ForestryItem.beverage.registerItem(new ItemBeverage(
						new BeverageInfo("meadShort", "glass", 0xec9a19, 0xffffff, 1, 0.2f, true),
						new BeverageInfo("meadCurative", "glass", 0xc5feff, 0xffffff, 1, 0.2f, true)),
				"beverage");
		ForestryItem.ambrosia.registerItem(new ItemAmbrosia().setIsDrink(), "ambrosia");
		ForestryItem.honeyPot.registerItem(new ItemForestryFood(2, 0.2f).setIsDrink(), "honeyPot");

		// / SEASONER
		Item infuser = new ItemWithGui(GuiId.InfuserGUI);
		ForestryItem.infuser.registerItem(infuser, "infuser");

		// Mead
		ItemStack meadBottle = ForestryItem.beverage.getItemStack();
		BeverageInfo.saveEffects(meadBottle, Collections.singletonList(BeverageEffect.weakAlcoholic));
	}

	@Override
	protected void preInit() {
		super.preInit();

		LiquidRegistryHelper.registerLiquidContainer(Fluids.SHORT_MEAD, Constants.BUCKET_VOLUME, ForestryItem.beverage.getItemStack(), new ItemStack(Items.glass_bottle));

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			BeverageManager.ingredientManager.addIngredient(ForestryItem.pollenCluster.getItemStack(1, 0), "Strong Curative");
			BeverageManager.ingredientManager.addIngredient(ForestryItem.pollenCluster.getItemStack(1, 1), "Weak Curative");
			BeverageManager.infuserManager.addMixture(1, ForestryItem.pollenCluster.getItemStack(1, 0), BeverageEffect.strongAntidote);
			BeverageManager.infuserManager.addMixture(1, ForestryItem.pollenCluster.getItemStack(1, 1), BeverageEffect.weakAntidote);
		}
	}

	@Override
	protected void registerRecipes() {
		// INFUSER
		RecipeUtil.addRecipe(ForestryItem.infuser.getItemStack(),
				"X", "#", "X",
				'#', "ingotIron",
				'X', "ingotBronze");
	}

	@Override
	public GuiHandlerBase getGuiHandler() {
		return new GuiHandlerFood();
	}
}
