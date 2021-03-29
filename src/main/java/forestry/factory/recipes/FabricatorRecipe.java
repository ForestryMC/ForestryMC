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

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

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

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FabricatorRecipe> {

		@Override
		public FabricatorRecipe read(ResourceLocation recipeId, JsonObject json) {
			Ingredient plan = RecipeSerializers.deserialize(json.get("plan"));
			FluidStack molten = RecipeSerializers.deserializeFluid(JSONUtils.getJsonObject(json, "molten"));
			ShapedRecipe internal = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, JSONUtils.getJsonObject(json, "recipe"));

			return new FabricatorRecipe(recipeId, plan, molten, internal);
		}

		@Override
		public FabricatorRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			Ingredient plan = Ingredient.read(buffer);
			FluidStack molten = buffer.readFluidStack();
			ShapedRecipe internal = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer);

			return new FabricatorRecipe(recipeId, plan, molten, internal);
		}

		@Override
		public void write(PacketBuffer buffer, FabricatorRecipe recipe) {
			recipe.getPlan().write(buffer);
			buffer.writeFluidStack(recipe.getLiquid());
			IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe.getCraftingGridRecipe());
		}
	}
}
