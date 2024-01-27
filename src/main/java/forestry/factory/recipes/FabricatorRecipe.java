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
package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorRecipe;

public class FabricatorRecipe implements IFabricatorRecipe {

	private final ResourceLocation id;
	private final Ingredient plan;
	private final FluidStack molten;
	private final ShapedRecipe recipe;

	public FabricatorRecipe(ResourceLocation id, Ingredient plan, FluidStack molten, ShapedRecipe recipe) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(plan);
		Preconditions.checkNotNull(molten);

		this.id = id;
		this.plan = plan;
		this.molten = molten;
		this.recipe = recipe;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public Ingredient getPlan() {
		return plan;
	}

	@Override
	public FluidStack getLiquid() {
		return molten;
	}

	@Override
	public ShapedRecipe getCraftingGridRecipe() {
		return recipe;
	}

	public static class Serializer implements RecipeSerializer<FabricatorRecipe> {

		@Override
		public FabricatorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient plan = RecipeSerializers.deserialize(json.get("plan"));
			FluidStack molten = RecipeSerializers.deserializeFluid(GsonHelper.getAsJsonObject(json, "molten"));
			ShapedRecipe internal = RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, GsonHelper.getAsJsonObject(json, "recipe"));

			return new FabricatorRecipe(recipeId, plan, molten, internal);
		}

		@Override
		public FabricatorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient plan = Ingredient.fromNetwork(buffer);
			FluidStack molten = buffer.readFluidStack();
			ShapedRecipe internal = RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer);

			return new FabricatorRecipe(recipeId, plan, molten, internal);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, FabricatorRecipe recipe) {
			recipe.getPlan().toNetwork(buffer);
			buffer.writeFluidStack(recipe.getLiquid());
			RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe.getCraftingGridRecipe());
		}
	}
}
