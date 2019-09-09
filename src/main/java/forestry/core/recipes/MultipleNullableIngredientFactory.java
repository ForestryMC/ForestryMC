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
//import com.google.gson.JsonParseException;
//import com.google.gson.JsonSyntaxException;
//
//import javax.annotation.Nonnull;
//import java.util.ArrayList;
//import java.util.List;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.Ingredient;
//import net.minecraft.util.JSONUtils;
//
//import net.minecraftforge.common.crafting.CraftingHelper;
//import net.minecraftforge.common.crafting.IIngredientFactory;
//import net.minecraftforge.common.crafting.IIngredientSerializer;
//import net.minecraftforge.common.crafting.JsonContext;
//
///**
// * For ingredients where one or more must be enabled but there may be ingredients which aren't available
// */
//@SuppressWarnings("unused")
//public class MultipleNullableIngredientFactory implements IIngredientSerializer {
//	@Nonnull
//	@Override
//	public Ingredient parse(JsonObject json) {
//		JsonArray values = JSONUtils.getJsonArray(json, "values");
//		if (values.size() == 0) {
//			throw new JsonParseException("No ingredients given for the recipe!");
//		}
//		List<Ingredient> ingredients = new ArrayList<>();
//		for (JsonElement element : values) {
//			if (!(element instanceof JsonObject)) {
//				throw new JsonSyntaxException("Ingredient not give as Json Object!");
//			}
//			ingredients.add(getIngredient((JsonObject) element, context));
//		}
//		return Ingredient.merge(ingredients);    //says internal but does what I think it should.
//	}
//
//	private static Ingredient getIngredient(JsonObject json, JsonContext context) {
//		if (!JSONUtils.hasField(json, "type")) {
//			//so it will use the type "minecraft:item"
//			try {
//				ItemStack stack = CraftingHelper.getItemStackBasic(json, context);
//				return Ingredient.fromStacks(stack);
//			} catch (JsonSyntaxException e) {
//				//item not found
//				return Ingredient.EMPTY;
//			}
//		}
//		return CraftingHelper.getIngredient(json, context);
//	}
//
//}
