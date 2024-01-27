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

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterRecipe;

public class CarpenterRecipe implements ICarpenterRecipe {

	private final ResourceLocation id;
	private final int packagingTime;
	private final FluidStack liquid;
	private final Ingredient box;
	private final CraftingRecipe recipe;
	private final ItemStack result;

	public CarpenterRecipe(ResourceLocation id, int packagingTime, FluidStack liquid, Ingredient box, CraftingRecipe recipe, @Nullable ItemStack result) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(box);
		Preconditions.checkNotNull(recipe);

		this.id = id;
		this.packagingTime = packagingTime;
		this.liquid = liquid;
		this.box = box;
		this.recipe = recipe;
		this.result = result != null ? result : recipe.getResultItem();
	}

	@Override
	public int getPackagingTime() {
		return packagingTime;
	}

	@Override
	public Ingredient getBox() {
		return box;
	}

	@Override
	public FluidStack getFluidResource() {
		return liquid;
	}

	@Override
	public CraftingRecipe getCraftingGridRecipe() {
		return recipe;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class Serializer implements RecipeSerializer<CarpenterRecipe> {

		@Override
		public CarpenterRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			int packagingTime = GsonHelper.getAsInt(json, "time");
			FluidStack liquid = json.has("liquid") ? RecipeSerializers.deserializeFluid(GsonHelper.getAsJsonObject(json, "liquid")) : FluidStack.EMPTY;
			Ingredient box = RecipeSerializers.deserialize(json.get("box"));
			CraftingRecipe internal = (CraftingRecipe) RecipeManager.fromJson(recipeId, GsonHelper.getAsJsonObject(json, "recipe"));
			ItemStack result = json.has("result") ? RecipeSerializers.item(GsonHelper.getAsJsonObject(json, "result")) : internal.getResultItem();

			return new CarpenterRecipe(recipeId, packagingTime, liquid, box, internal, result);
		}

		@Override
		public CarpenterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			int packagingTime = buffer.readVarInt();
			FluidStack liquid = buffer.readBoolean() ? FluidStack.readFromPacket(buffer) : FluidStack.EMPTY;
			Ingredient box = Ingredient.fromNetwork(buffer);
			CraftingRecipe internal = (CraftingRecipe) ClientboundUpdateRecipesPacket.fromNetwork(buffer);
			ItemStack result = buffer.readItem();

			return new CarpenterRecipe(recipeId, packagingTime, liquid, box, internal, result);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, CarpenterRecipe recipe) {
			buffer.writeVarInt(recipe.packagingTime);

			if (!recipe.liquid.isEmpty()) {
				buffer.writeBoolean(true);
				recipe.liquid.writeToPacket(buffer);
			} else {
				buffer.writeBoolean(false);
			}

			recipe.box.toNetwork(buffer);
			ClientboundUpdateRecipesPacket.toNetwork(buffer, recipe.recipe);
			buffer.writeItem(recipe.result);
		}
	}
}
