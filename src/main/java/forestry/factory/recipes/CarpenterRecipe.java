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
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
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
	private final ShapedRecipe recipe;
	private ItemStack result;

	public CarpenterRecipe(ResourceLocation id, int packagingTime, @Nullable FluidStack liquid, Ingredient box, ShapedRecipe recipe, @Nullable ItemStack result) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(box);
		Preconditions.checkNotNull(recipe);

		this.id = id;
		this.packagingTime = packagingTime;
		this.liquid = liquid;
		this.box = box;
		this.recipe = recipe;
		this.result = result != null ? result : recipe.getRecipeOutput();
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
	public ShapedRecipe getCraftingGridRecipe() {
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
		public CarpenterRecipe read(ResourceLocation recipeId, JsonObject json) {
			int packagingTime = JSONUtils.getInt(json, "time");
			FluidStack liquid = json.has("liquid") ? RecipeSerializers.deserializeFluid(JSONUtils.getJsonObject(json, "liquid")) : null;
			Ingredient box = Ingredient.deserialize(json.get("box"));
			ShapedRecipe internal = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, JSONUtils.getJsonObject(json, "recipe"));
			ItemStack result = json.has("result") ? ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result")) : internal.getRecipeOutput();

			return new CarpenterRecipe(recipeId, packagingTime, liquid, box, internal, result);
		}

		@Override
		public CarpenterRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			int packagingTime = buffer.readVarInt();
			FluidStack liquid = buffer.readBoolean() ? FluidStack.readFromPacket(buffer) : null;
			Ingredient box = Ingredient.read(buffer);
			ShapedRecipe internal = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer);
			ItemStack result = buffer.readItemStack();

			return new CarpenterRecipe(recipeId, packagingTime, liquid, box, internal, result);
		}

		@Override
		public void write(PacketBuffer buffer, CarpenterRecipe recipe) {
			buffer.writeVarInt(recipe.packagingTime);

			if (recipe.liquid != null) {
				buffer.writeBoolean(true);
				recipe.liquid.writeToPacket(buffer);
			} else {
				buffer.writeBoolean(false);
			}

			recipe.box.write(buffer);
			IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe.recipe);
			buffer.writeItemStack(recipe.result);
		}
	}
}
