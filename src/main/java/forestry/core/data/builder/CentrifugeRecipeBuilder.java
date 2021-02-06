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

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class CentrifugeRecipeBuilder {

	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
	private int processingTime;
	private ItemStack input;
	private final NonNullList<ICentrifugeRecipe.Product> outputs = NonNullList.create();

	public CentrifugeRecipeBuilder setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	public CentrifugeRecipeBuilder setInput(ItemStack input) {
		this.input = input;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
				.withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
				.withRewards(AdvancementRewards.Builder.recipe(id))
				.withRequirementsStrategy(IRequirementsStrategy.OR);
		consumer.accept(new Result(id, processingTime, input, outputs, advancementBuilder, null));
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final int processingTime;
		private final ItemStack input;
		private final NonNullList<ICentrifugeRecipe.Product> outputs;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation id, int processingTime, ItemStack input, NonNullList<ICentrifugeRecipe.Product> outputs, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
			this.id = id;
			this.processingTime = processingTime;
			this.input = input;
			this.outputs = outputs;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
		}

		@Override
		public void serialize(JsonObject json) {
			json.addProperty("time", processingTime);
			json.add("input", RecipeSerializers.item(input));

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
			return advancementBuilder.serialize();
		}

		@Override
		public ResourceLocation getAdvancementID() {
			return advancementId;
		}
	}
}
