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
package forestry.core.data.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class CentrifugeRecipeBuilder {

	private int processingTime;
	private Ingredient input;
	private final NonNullList<ICentrifugeRecipe.Product> outputs = NonNullList.create();

	public CentrifugeRecipeBuilder setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	public CentrifugeRecipeBuilder setInput(Ingredient input) {
		this.input = input;
		return this;
	}

	public CentrifugeRecipeBuilder product(float chance, ItemStack itemStack) {
		outputs.add(new ICentrifugeRecipe.Product(chance, itemStack));
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, processingTime, input, outputs));
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final int processingTime;
		private final Ingredient input;
		private final NonNullList<ICentrifugeRecipe.Product> outputs;

		public Result(ResourceLocation id, int processingTime, Ingredient input, NonNullList<ICentrifugeRecipe.Product> outputs) {
			this.id = id;
			this.processingTime = processingTime;
			this.input = input;
			this.outputs = outputs;
		}

		@Override
		public void serialize(JsonObject json) {
			json.addProperty("time", processingTime);
			json.add("input", input.serialize());

			JsonArray products = new JsonArray();

			for (ICentrifugeRecipe.Product product : outputs) {
				JsonObject object = new JsonObject();
				object.addProperty("chance", product.getProbability());
				object.add("item", RecipeSerializers.item(product.getStack()));
				products.add(object);
			}

			json.add("products", products);
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return ICentrifugeRecipe.Companion.SERIALIZER;
		}

		@Override
		public JsonObject getAdvancementJson() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementID() {
			return null;
		}
	}
}
