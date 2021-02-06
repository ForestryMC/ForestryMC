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
import com.google.gson.JsonElement;
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
	private final ShapedRecipe ingredients;

	public FabricatorRecipe(ResourceLocation id, Ingredient plan, FluidStack molten, ShapedRecipe ingredients) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(plan);
		Preconditions.checkNotNull(molten);
		Preconditions.checkNotNull(ingredients);

		this.id = id;
		this.plan = plan;
		this.molten = molten;
		this.ingredients = ingredients;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public FluidStack getLiquid() {
		return molten;
	}

	@Override
	public ShapedRecipe getCraftingGridRecipe() {
		return ingredients;
	}

	@Override
	public Ingredient getPlan() {
		return plan;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FabricatorRecipe> {
		@Override
		public FabricatorRecipe read(ResourceLocation recipeId, JsonObject json) {
			JsonElement planElement = json.get("plan");
			Ingredient plan = Ingredient.EMPTY;
			if (planElement != null) {
				plan = Ingredient.deserialize(planElement);
			}

			FluidStack molten = RecipeSerializers.deserializeFluid(JSONUtils.getJsonObject(json, "molten"));

			ShapedRecipe ingredients = IRecipeSerializer.CRAFTING_SHAPED
					.read(recipeId, JSONUtils.getJsonObject(json, "ingredients"));

			return new FabricatorRecipe(recipeId, plan, molten, ingredients);
		}

		@Override
		public FabricatorRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			int packagingTime = buffer.readVarInt();
			FluidStack molten = FluidStack.readFromPacket(buffer);
			Ingredient plan = Ingredient.read(buffer);
			ShapedRecipe ingredients = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer);

			return new FabricatorRecipe(recipeId, plan, molten, ingredients);
		}

		@Override
		public void write(PacketBuffer buffer, FabricatorRecipe recipe) {
			recipe.molten.writeToPacket(buffer);
			recipe.plan.write(buffer);
			IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe.ingredients);
		}
	}
}
