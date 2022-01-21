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

import java.util.function.Consumer;

import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class FabricatorRecipeBuilder {

	private Ingredient plan;
	private FluidStack molten;
	private ShapedRecipeBuilder.Result recipe;

	public FabricatorRecipeBuilder setPlan(Ingredient plan) {
		this.plan = plan;
		return this;
	}

	public FabricatorRecipeBuilder setMolten(FluidStack molten) {
		this.molten = molten;
		return this;
	}

	public FabricatorRecipeBuilder recipe(ShapedRecipeBuilder recipe) {
		Holder<FinishedRecipe> holder = new Holder<>();
		recipe.unlockedBy("impossible", new ImpossibleTrigger.TriggerInstance()).save(holder::set);
		this.recipe = (ShapedRecipeBuilder.Result) holder.get();
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, plan, molten, recipe));
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final Ingredient plan;
		private final FluidStack molten;
		private final ShapedRecipeBuilder.Result recipe;

		public Result(ResourceLocation id, Ingredient plan, FluidStack molten, ShapedRecipeBuilder.Result recipe) {
			this.id = id;
			this.plan = plan;
			this.molten = molten;
			this.recipe = recipe;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("plan", plan.toJson());
			json.add("molten", RecipeSerializers.serializeFluid(molten));
			json.add("recipe", recipe.serializeRecipe());
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return IFabricatorRecipe.Companion.SERIALIZER;
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
