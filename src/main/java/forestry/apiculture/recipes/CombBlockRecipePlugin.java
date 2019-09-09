///*******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// ******************************************************************************/
//package forestry.apiculture.recipes;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import forestry.apiculture.items.EnumHoneyComb;
//
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.IModRegistry;
//import mezz.jei.api.JEIPlugin;
//import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
//
//@JEIPlugin
//public class CombBlockRecipePlugin implements IModPlugin {
//
//	@Override
//	public void register(IModRegistry registry) {
//		Set<CombBlockRecipeWrapper> recipes = new HashSet<>();
//		for (int i = 0; i < EnumHoneyComb.values().length; i++) {
//			recipes.add(new CombBlockRecipeWrapper(i));
//		}
//		registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
//	}
//
//}
