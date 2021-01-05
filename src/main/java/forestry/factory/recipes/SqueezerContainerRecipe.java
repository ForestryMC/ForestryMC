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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import forestry.api.recipes.ISqueezerContainerRecipe;

public class SqueezerContainerRecipe implements ISqueezerContainerRecipe {

	private final ResourceLocation id;
	private final ItemStack emptyContainer;
	private final int processingTime;
	private final ItemStack remnants;
	private final float remnantsChance;

	public SqueezerContainerRecipe(ResourceLocation id, ItemStack emptyContainer, int processingTime, ItemStack remnants, float remnantsChance) {
		this.id = id;
		Preconditions.checkNotNull(emptyContainer);
		Preconditions.checkArgument(!emptyContainer.isEmpty());
		Preconditions.checkNotNull(remnants);

		this.emptyContainer = emptyContainer;
		this.processingTime = processingTime;
		this.remnants = remnants;
		this.remnantsChance = remnantsChance;
	}

	@Override
	public ItemStack getEmptyContainer() {
		return emptyContainer;
	}

	@Override
	public NonNullList<ItemStack> getResources() {
		return NonNullList.create();
	}

	@Override
	public int getProcessingTime() {
		return processingTime;
	}

	@Override
	public ItemStack getRemnants() {
		return remnants;
	}

	@Override
	public float getRemnantsChance() {
		return remnantsChance;
	}

	@Override
	public FluidStack getFluidOutput() {
		return FluidStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SqueezerContainerRecipe> {

		@Override
		public SqueezerContainerRecipe read(ResourceLocation recipeId, JsonObject json) {
			ItemStack emptyContainer = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "container"), true);
			int processingTime = JSONUtils.getInt(json, "time");
			ItemStack remnants = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "remnants"), true);
			float remnantsChance = JSONUtils.getFloat(json, "remnantsChance");

			return new SqueezerContainerRecipe(recipeId, emptyContainer, processingTime, remnants, remnantsChance);
		}

		@Override
		public SqueezerContainerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			ItemStack emptyContainer = buffer.readItemStack();
			int processingTime = buffer.readVarInt();
			ItemStack remnants = buffer.readItemStack();
			float remnantsChance = buffer.readFloat();

			return new SqueezerContainerRecipe(recipeId, emptyContainer, processingTime, remnants, remnantsChance);
		}

		@Override
		public void write(PacketBuffer buffer, SqueezerContainerRecipe recipe) {
			buffer.writeItemStack(recipe.emptyContainer);
			buffer.writeVarInt(recipe.processingTime);
			buffer.writeItemStack(recipe.remnants);
			buffer.writeFloat(recipe.remnantsChance);
		}
	}
}
