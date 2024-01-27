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
package forestry.core.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ItemLike;
import net.minecraft.util.GsonHelper;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class FallbackIngredient extends Ingredient {
	private final Ingredient primary;
	private final Ingredient fallback;

	public static Ingredient fromItems(ItemLike primary, ItemLike fallback) {
		return fromIngredients(Ingredient.of(primary), Ingredient.of(fallback));
	}

	public static Ingredient fromStacks(ItemStack primary, ItemStack fallback) {
		return fromIngredients(Ingredient.of(primary), Ingredient.of(fallback));
	}

	public static Ingredient fromTag(TagKey<Item> primary, ItemStack fallback) {
		return fromIngredients(Ingredient.of(primary), Ingredient.of(fallback));
	}

	public static Ingredient fromTag(TagKey<Item> primary, TagKey<Item> fallback) {
		return fromIngredients(Ingredient.of(primary), Ingredient.of(fallback));
	}

	public static Ingredient fromIngredients(Ingredient primary, Ingredient fallback) {
		return new FallbackIngredient(primary, fallback);
	}

	private FallbackIngredient(Ingredient primary, Ingredient fallback) {
		super(Stream.of());
		this.primary = primary;
		this.fallback = fallback;
	}

	@Override
	public JsonElement toJson() {
		JsonObject jsonobject = new JsonObject();
		jsonobject.add("primary", primary.toJson());
		jsonobject.add("fallback", fallback.toJson());
		return jsonobject;
	}

	public static class Serializer implements IIngredientSerializer<Ingredient> {
		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {
		}

		@Override
		public Ingredient parse(FriendlyByteBuf buffer) {
			return Ingredient.fromNetwork(buffer);
		}

		@Override
		public void write(FriendlyByteBuf buffer, Ingredient ingredient) {
			ingredient.toNetwork(buffer);
		}

		@Nonnull
		@Override
		public Ingredient parse(JsonObject json) {
			Ingredient ret;
			try {
				JsonArray arr = GsonHelper.getAsJsonArray(json, "primary");
				List<Ingredient> ingredientList = new ArrayList<>();
				for (JsonElement element : arr) {
					if (!(element instanceof JsonObject obj)) {
						throw new JsonSyntaxException("Didn't supply json object for ingredient!");
					}
					ingredientList.add(CraftingHelper.getIngredient(obj));
				}
				ret = Ingredient.merge(ingredientList);
			} catch (JsonSyntaxException e) {
				ret = Ingredient.EMPTY;    //throws exception if item doesn't exist
			}
			if (ret.getItems().length == 0) {
				JsonArray fallbackArr = GsonHelper.getAsJsonArray(json, "fallback");
				List<Ingredient> ingredients = new ArrayList<>();
				for (JsonElement element : fallbackArr) {
					if (!(element instanceof JsonObject obj)) {
						throw new JsonSyntaxException("Didn't supply json object for ingredient!");
					}
					ingredients.add(CraftingHelper.getIngredient(obj));
				}
				ret = Ingredient.merge(ingredients);
			}
			return ret;
		}
	}
}
