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

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import forestry.api.recipes.IFabricatorRecipe;

public class FabricatorRecipe implements IFabricatorRecipe {

	private final ResourceLocation id;
	private final ItemStack plan;
	private final FluidStack molten;
	private final NonNullList<Ingredient> ingredients;
	private final NonNullList<String> oreDicts;
	private final ItemStack result;
	private final int width;
	private final int height;

	public FabricatorRecipe(ResourceLocation id, ItemStack plan, FluidStack molten, ItemStack result, NonNullList<Ingredient> ingredients, NonNullList<String> oreDicts, int width, int height) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(plan);
		Preconditions.checkNotNull(molten);
		Preconditions.checkNotNull(result);
		Preconditions.checkArgument(!result.isEmpty());
		Preconditions.checkNotNull(ingredients);
		Preconditions.checkNotNull(oreDicts);
		Preconditions.checkArgument(width > 0);
		Preconditions.checkArgument(height > 0);

		this.id = id;
		this.plan = plan;
		this.molten = molten;
		this.result = result;
		this.ingredients = ingredients;
		this.oreDicts = oreDicts;
		this.width = width;
		this.height = height;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public NonNullList<String> getOreDicts() {
		return oreDicts;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public ItemStack getPlan() {
		return plan;
	}

	@Override
	public FluidStack getLiquid() {
		return molten;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return result;
	}

	// TODO: Remove references of OreDict
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FabricatorRecipe> {

		@Override
		public FabricatorRecipe read(ResourceLocation recipeId, JsonObject json) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FabricatorRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void write(PacketBuffer buffer, FabricatorRecipe recipe) {
			throw new UnsupportedOperationException();
		}
	}
}
