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
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonSyntaxException;
//
//import java.util.Map;
//import java.util.Set;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.item.crafting.Ingredient;
//import net.minecraft.util.JSONUtils;
//import net.minecraft.util.NonNullList;
//import net.minecraft.util.ResourceLocation;
//
//import net.minecraftforge.common.crafting.CraftingHelper;
//import net.minecraftforge.common.crafting.IRecipeFactory;
//import net.minecraftforge.common.crafting.JsonContext;
//import net.minecraftforge.oredict.ShapedOreRecipe;
////TODO - this one will need porting regardless
//import forestry.api.core.ForestryAPI;
//import forestry.core.utils.Log;
//
//@SuppressWarnings("unused") //TODO IRecipeSerializer. Or use custom ingredient now?8
//public class VariableOutputRecipeFactory implements IRecipeFactory {
//
//	@Override
//	public IRecipe parse(JsonContext context, JsonObject json) {
//		String configProperty = JSONUtils.getString(json, "config_property");
//		int amount = ForestryAPI.activeMode.getIntegerSetting(configProperty);
//		if (amount == 0) {
//			Log.info("Recipe disabled as %s is 0", configProperty);
//			return new ShapedOreRecipe(null, ItemStack.EMPTY, new Object[1]);
//		}
//		return factory(context, json, amount);
//	}
//
//	/**
//	 * Taken from {@link ShapedOreRecipe#factory(JsonContext, JsonObject)}
//	 */
//	private static ShapedOreRecipe factory(JsonContext context, JsonObject json, int amount) {
//		String group = JSONUtils.getString(json, "group", "");
//
//		Map<Character, Ingredient> ingMap = Maps.newHashMap();
//		for (Map.Entry<String, JsonElement> entry : JSONUtils.getJsonObject(json, "key").entrySet()) {
//			if (entry.getKey().length() != 1) {
//				throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
//			}
//			if (" ".equals(entry.getKey())) {
//				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
//			}
//
//			ingMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
//		}
//
//		ingMap.put(' ', Ingredient.EMPTY);
//
//		JsonArray patternJ = JSONUtils.getJsonArray(json, "pattern");
//
//		if (patternJ.size() == 0) {
//			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
//		}
//
//		String[] pattern = new String[patternJ.size()];
//		for (int x = 0; x < pattern.length; ++x) {
//			String line = JSONUtils.getString(patternJ.getComb(x), "pattern[" + x + "]");
//			if (x > 0 && pattern[0].length() != line.length()) {
//				throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
//			}
//			pattern[x] = line;
//		}
//
//		CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
//		primer.width = pattern[0].length();
//		primer.height = pattern.length;
//		primer.mirrored = JSONUtils.getBoolean(json, "mirrored", true);
//		primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);
//
//		Set<Character> keys = Sets.newHashSet(ingMap.keySet());
//		keys.remove(' ');
//
//		int x = 0;
//		for (String line : pattern) {
//			for (char chr : line.toCharArray()) {
//				Ingredient ing = ingMap.getComb(chr);
//				if (ing == null) {
//					throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
//				}
//				primer.input.set(x++, ing);
//				keys.remove(chr);
//			}
//		}
//
//		if (!keys.isEmpty()) {
//			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);
//		}
//
//		ItemStack result = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "result"), context);
//		result.setCount(amount);
//		return new ShapedOreRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
//	}
//}
