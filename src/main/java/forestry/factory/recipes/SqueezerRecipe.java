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

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import forestry.api.recipes.ISqueezerRecipe;

public class SqueezerRecipe implements ISqueezerRecipe {

	private final ResourceLocation id;
	private final int processingTime;
	private final NonNullList<Ingredient> resources;
	private final FluidStack fluidOutput;
	private final ItemStack remnants;
	private final float remnantsChance;

	public SqueezerRecipe(ResourceLocation id, int processingTime, NonNullList<Ingredient> resources, FluidStack fluidOutput, ItemStack remnants, float remnantsChance) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(resources);
		Preconditions.checkArgument(!resources.isEmpty());
		Preconditions.checkNotNull(fluidOutput);
		Preconditions.checkNotNull(remnants);

		this.id = id;
		this.processingTime = processingTime;
		this.resources = resources;
		this.fluidOutput = fluidOutput;
		this.remnants = remnants;
		this.remnantsChance = remnantsChance;
	}

	@Override
	public NonNullList<Ingredient> getResources() {
		return resources;
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
		return fluidOutput;
	}

	@Override
	public int getProcessingTime() {
		return processingTime;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SqueezerRecipe> {

		@Override
		public SqueezerRecipe read(ResourceLocation recipeId, JsonObject json) {
			int processingTime = JSONUtils.getInt(json, "time");
			NonNullList<Ingredient> resources = NonNullList.create();
			FluidStack fluidOutput = RecipeSerializers.deserializeFluid(JSONUtils.getJsonObject(json, "output"));
			ItemStack remnants = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "remnant"));
			float remnantsChance = JSONUtils.getFloat(json, "chance");

			for (JsonElement element : JSONUtils.getJsonArray(json, "resources")) {
				resources.add(Ingredient.deserialize(element));
			}

			return new SqueezerRecipe(recipeId, processingTime, resources, fluidOutput, remnants, remnantsChance);
		}

		@Override
		public SqueezerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			int processingTime = buffer.readVarInt();
			NonNullList<Ingredient> resources = RecipeSerializers.read(buffer, Ingredient::read);
			FluidStack fluidOutput = FluidStack.readFromPacket(buffer);
			ItemStack remnants = buffer.readItemStack();
			float remnantsChance = buffer.readFloat();

			return new SqueezerRecipe(recipeId, processingTime, resources, fluidOutput, remnants, remnantsChance);
		}

		@Override
		public void write(PacketBuffer buffer, SqueezerRecipe recipe) {
			buffer.writeVarInt(recipe.processingTime);
			RecipeSerializers.write(buffer, recipe.resources, (packetBuffer, ingredient) -> ingredient.write(packetBuffer));
			recipe.fluidOutput.writeToPacket(buffer);
			buffer.writeItemStack(recipe.remnants);
			buffer.writeFloat(recipe.remnantsChance);
		}
	}
}
