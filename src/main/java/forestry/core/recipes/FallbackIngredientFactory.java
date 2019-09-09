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
//package forestry.core.recipes;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonSyntaxException;
//
//import javax.annotation.Nonnull;
//import java.util.ArrayList;
//import java.util.List;
//
//import net.minecraft.item.crafting.Ingredient;
//import net.minecraft.util.JSONUtils;
//
//import net.minecraftforge.common.crafting.CraftingHelper;
//import net.minecraftforge.common.crafting.IIngredientFactory;
//import net.minecraftforge.common.crafting.IIngredientSerializer;
//import net.minecraftforge.common.crafting.JsonContext;
////TODO - will also need porting
//@SuppressWarnings("unused")
//public class FallbackIngredientFactory implements IIngredientSerializer {
//	@Nonnull
//	@Override
//	public Ingredient parse(JsonObject json) {
//		Ingredient ret;
//		try {
//			JsonArray arr = JSONUtils.getJsonArray(json, "primary");
//			List<Ingredient> ingredientList = new ArrayList<>();
//			for (JsonElement element : arr) {
//				if (!(element instanceof JsonObject)) {
//					throw new JsonSyntaxException("Didn't supply json object for ingredient!");
//				}
//				JsonObject obj = (JsonObject) element;
//				ingredientList.add(CraftingHelper.getIngredient(obj, context));
//			}
//			ret = Ingredient.merge(ingredientList);
//		} catch (JsonSyntaxException e) {
//			ret = Ingredient.EMPTY;    //throws exception if item doesn't exist
//		}
//		if (ret.getMatchingStacks().length == 0) {
//			JsonArray fallbackArr = JSONUtils.getJsonArray(json, "fallback");
//			List<Ingredient> ingredients = new ArrayList<>();
//			for (JsonElement element : fallbackArr) {
//				if (!(element instanceof JsonObject)) {
//					throw new JsonSyntaxException("Didn't supply json object for ingredient!");
//				}
//				JsonObject obj = (JsonObject) element;
//				ingredients.add(CraftingHelper.getIngredient(obj, context));
//			}
//			ret = Ingredient.merge(ingredients);
//		}
//		return ret;
//	}
//}
