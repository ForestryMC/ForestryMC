/*
 *******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 *******************************************************************************
 */
package forestry.core.recipes.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;

import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

import forestry.api.arboriculture.WoodBlockKind;

/**
 * Very messy, there's probably a lot that can be done to clean this up
 */
public class WoodTypeRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {

		//pattern
		String[] pattern = null;
		JsonArray elements = JsonUtils.getJsonArray(json, "pattern", new JsonArray());
		if (elements.size() > 0) {
			pattern = new String[elements.size()];
			for (int x = 0; x < pattern.length; ++x) {
				String line = JsonUtils.getString(elements.get(x), "pattern[" + x + "]");
				if (x > 0 && pattern[0].length() != line.length()) {
					throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
				}
				pattern[x] = line;
			}
		}

		JsonObject input = JsonUtils.getJsonObject(json, "input");
		JsonObject output = JsonUtils.getJsonObject(json, "output");
		WoodBlockKind inputKind = Enum.valueOf(WoodBlockKind.class, JsonUtils.getString(input, "blockKind"));
		WoodBlockKind outputKind = Enum.valueOf(WoodBlockKind.class, JsonUtils.getString(output, "blockKind"));
		boolean inputFireproof = JsonUtils.getBoolean(input, "fireproof");
		boolean outputFireproof = JsonUtils.getBoolean(output, "fireproof");
		int outputCount = JsonUtils.getInt(output, "count", 1);

		return pattern == null ? new WoodTypeRecipeShapeless(inputKind, outputKind, inputFireproof, outputFireproof, outputCount) :
				new WoodTypeRecipe(inputKind, outputKind, inputFireproof, outputFireproof, pattern, outputCount);
	}
}
