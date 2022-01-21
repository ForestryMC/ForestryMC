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

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class CarpenterRecipeBuilder {

	private int packagingTime;
	@Nullable
	private FluidStack liquid;
	private Ingredient box;
	private FinishedRecipe recipe;
	@Nullable
	private ItemStack result;

	public CarpenterRecipeBuilder setPackagingTime(int packagingTime) {
		this.packagingTime = packagingTime;
		return this;
	}

	public CarpenterRecipeBuilder setLiquid(@Nullable FluidStack liquid) {
		this.liquid = liquid;
		return this;
	}

	public CarpenterRecipeBuilder setBox(Ingredient box) {
		this.box = box;
		return this;
	}

	public CarpenterRecipeBuilder recipe(ShapedRecipeBuilder recipe) {
		Holder<FinishedRecipe> holder = new Holder<>();
		recipe.unlockedBy("impossible", new ImpossibleTrigger.TriggerInstance()).save(holder::set);
		this.recipe = holder.get();
		return this;
	}

	public CarpenterRecipeBuilder recipe(ShapelessRecipeBuilder recipe) {
		Holder<FinishedRecipe> holder = new Holder<>();
		recipe.unlockedBy("impossible", new ImpossibleTrigger.TriggerInstance()).save(holder::set);
		this.recipe = holder.get();
		return this;
	}

	/**
	 * In case the recipe should create an item stack, and not a basic item without NBT
	 *
	 * @param result The result to override {@link #recipe(ShapedRecipeBuilder)}
	 * @return This builder for chaining
	 */
	public CarpenterRecipeBuilder override(ItemStack result) {
		this.result = result;
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, packagingTime, liquid, box, recipe, result));
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final int packagingTime;
		@Nullable
		private final FluidStack liquid;
		private final Ingredient box;
		private final FinishedRecipe recipe;
		@Nullable
		private final ItemStack result;

		public Result(ResourceLocation id, int packagingTime, @Nullable FluidStack liquid, Ingredient box, FinishedRecipe recipe, @Nullable ItemStack result) {
			this.id = id;
			this.packagingTime = packagingTime;
			this.liquid = liquid;
			this.box = box;
			this.recipe = recipe;
			this.result = result;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.addProperty("time", packagingTime);

			if (liquid != null) {
				json.add("liquid", RecipeSerializers.serializeFluid(liquid));
			}

			json.add("box", box.toJson());
			json.add("recipe", recipe.serializeRecipe());

			if (result != null) {
				json.add("result", RecipeSerializers.item(result));
			}
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return ICarpenterRecipe.Companion.SERIALIZER;
		}

		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
