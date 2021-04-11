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

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import forestry.api.recipes.ICarpenterRecipe;

public class CarpenterRecipe implements ICarpenterRecipe {

	private final ResourceLocation id;
	private final int packagingTime;
	@Nullable
	private final FluidStack liquid;
	private final Ingredient box;
	private final ICraftingRecipe recipe;
	private final ItemStack result;

	public CarpenterRecipe(ResourceLocation id, int packagingTime, @Nullable FluidStack liquid, Ingredient box, ICraftingRecipe recipe, @Nullable ItemStack result) {
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
	@Nullable
	public FluidStack getFluidResource() {
		return liquid;
	}

	@Override
	public ICraftingRecipe getCraftingGridRecipe() {
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

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CarpenterRecipe> {

		@Override
		public CarpenterRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			int packagingTime = JSONUtils.getAsInt(json, "time");
			FluidStack liquid = json.has("liquid") ? RecipeSerializers.deserializeFluid(JSONUtils.getAsJsonObject(json, "liquid")) : null;
			Ingredient box = RecipeSerializers.deserialize(json.get("box"));
			ICraftingRecipe internal = (ICraftingRecipe) RecipeManager.fromJson(recipeId, JSONUtils.getAsJsonObject(json, "recipe"));
			ItemStack result = json.has("result") ? RecipeSerializers.item(JSONUtils.getAsJsonObject(json, "result")) : internal.getResultItem();

			return new CarpenterRecipe(recipeId, packagingTime, liquid, box, internal, result);
		}

		@Override
		public CarpenterRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			int packagingTime = buffer.readVarInt();
			FluidStack liquid = buffer.readBoolean() ? FluidStack.readFromPacket(buffer) : null;
			Ingredient box = Ingredient.fromNetwork(buffer);
			ICraftingRecipe internal = (ICraftingRecipe) SUpdateRecipesPacket.fromNetwork(buffer);
			ItemStack result = buffer.readItem();

			return new CarpenterRecipe(recipeId, packagingTime, liquid, box, internal, result);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, CarpenterRecipe recipe) {
			buffer.writeVarInt(recipe.packagingTime);

			if (recipe.liquid != null) {
				buffer.writeBoolean(true);
				recipe.liquid.writeToPacket(buffer);
			} else {
				buffer.writeBoolean(false);
			}

			recipe.box.toNetwork(buffer);
			SUpdateRecipesPacket.toNetwork(recipe.recipe, buffer);
			buffer.writeItem(recipe.result);
		}
	}
}
