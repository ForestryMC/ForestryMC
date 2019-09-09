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
//package forestry.arboriculture.recipes;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonSyntaxException;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.util.JSONUtils;
//
//import net.minecraftforge.common.crafting.IRecipeFactory;
//import net.minecraftforge.common.crafting.JsonContext;
//
//import forestry.api.arboriculture.WoodBlockKind;
//
///**
// * Very messy, there's probably a lot that can be done to clean this up
// */
//public class WoodTypeRecipeFactory implements IRecipeFactory {
//
//	public static final Set<WoodTypeRecipeBase> RECIPES = new HashSet<>();
//
//
//	@Override
//	public IRecipe parse(JsonContext context, JsonObject json) {
//
//		//pattern
//		String[] pattern = null;
//		JsonArray elements = JSONUtils.getJsonArray(json, "pattern", new JsonArray());
//		if (elements.size() > 0) {
//			pattern = new String[elements.size()];
//			for (int x = 0; x < pattern.length; ++x) {
//				String line = JSONUtils.getString(elements.get(x), "pattern[" + x + "]");
//				if (x > 0 && pattern[0].length() != line.length()) {
//					throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
//				}
//				pattern[x] = line;
//			}
//		}
//
//		JsonObject input = JSONUtils.getJsonObject(json, "input");
//		JsonObject output = JSONUtils.getJsonObject(json, "output");
//		WoodBlockKind inputKind = Enum.valueOf(WoodBlockKind.class, JSONUtils.getString(input, "blockKind"));
//		WoodBlockKind outputKind = Enum.valueOf(WoodBlockKind.class, JSONUtils.getString(output, "blockKind"));
//		boolean inputFireproof = JSONUtils.getBoolean(input, "fireproof");
//		boolean outputFireproof = JSONUtils.getBoolean(output, "fireproof");
//		int outputCount = JSONUtils.getInt(output, "count", 1);
//
//		WoodTypeRecipeBase recipe = pattern == null ? new WoodTypeRecipeShapeless(inputKind, outputKind, inputFireproof, outputFireproof, outputCount) :
//				new WoodTypeRecipe(inputKind, outputKind, inputFireproof, outputFireproof, pattern, outputCount);
//		RECIPES.add(recipe);
//		return recipe;
//	}
//}
