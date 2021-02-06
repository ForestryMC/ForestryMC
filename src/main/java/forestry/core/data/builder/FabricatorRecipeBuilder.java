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

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class FabricatorRecipeBuilder {

	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
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

	public FabricatorRecipeBuilder recipe(Consumer<Consumer<IFinishedRecipe>> consumer) {
		Holder<IFinishedRecipe> holder = new Holder<>();
		consumer.accept(holder::set);
		this.recipe = (ShapedRecipeBuilder.Result) holder.get();
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
				.withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
				.withRewards(AdvancementRewards.Builder.recipe(id))
				.withRequirementsStrategy(IRequirementsStrategy.OR);
		consumer.accept(new Result(id, plan, molten, recipe, advancementBuilder, null));
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final Ingredient plan;
		private final FluidStack molten;
		private final ShapedRecipeBuilder.Result recipe;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation id, Ingredient plan, FluidStack molten, ShapedRecipeBuilder.Result recipe, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
			this.id = id;
			this.plan = plan;
			this.molten = molten;
			this.recipe = recipe;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
		}

		@Override
		public void serialize(JsonObject json) {
			json.add("plan", plan.serialize());
			json.add("molten", RecipeSerializers.serializeFluid(molten));
			json.add("recipe", recipe.getRecipeJson());
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return IFabricatorRecipe.Companion.SERIALIZER;
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
