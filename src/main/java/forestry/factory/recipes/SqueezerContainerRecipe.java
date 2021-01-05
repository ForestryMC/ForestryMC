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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistryEntry;

import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.ISqueezerContainerRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.utils.ItemStackUtil;

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

	//TODO optional might be nice here
	@Override
	@Nullable
	public ISqueezerRecipe getSqueezerRecipe(ItemStack filledContainer) {
		if (filledContainer.isEmpty()) {
			return null;
		}
		LazyOptional<FluidStack> fluidOutput = FluidUtil.getFluidContained(filledContainer);

		return fluidOutput.map(f -> {
			ItemStack filledContainerCopy = ItemStackUtil.createCopyWithCount(filledContainer, 1);
			NonNullList<ItemStack> input = NonNullList.create();
			input.add(filledContainerCopy);
			return new SqueezerRecipe(IForestryRecipe.anonymous(), processingTime, input, f, remnants, remnantsChance);
		}).orElse(null);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SqueezerContainerRecipe> {

		@Override
		public SqueezerContainerRecipe read(ResourceLocation recipeId, JsonObject json) {
			return null;
		}

		@Override
		public SqueezerContainerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			return null;
		}

		@Override
		public void write(PacketBuffer buffer, SqueezerContainerRecipe recipe) {
		}
	}
}
