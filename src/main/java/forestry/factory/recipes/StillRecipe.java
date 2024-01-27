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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IStillRecipe;

public class StillRecipe implements IStillRecipe {

	private final ResourceLocation id;
	private final int timePerUnit;
	private final FluidStack input;
	private final FluidStack output;

	public StillRecipe(ResourceLocation id, int timePerUnit, FluidStack input, FluidStack output) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(input, "Still recipes need an input. Input was null.");
		Preconditions.checkNotNull(output, "Still recipes need an output. Output was null.");

		this.id = id;
		this.timePerUnit = timePerUnit;
		this.input = input;
		this.output = output;
	}

	@Override
	public int getCyclesPerUnit() {
		return timePerUnit;
	}

	@Override
	public FluidStack getInput() {
		return input;
	}

	@Override
	public FluidStack getOutput() {
		return output;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class Serializer implements RecipeSerializer<StillRecipe> {

		@Override
		public StillRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			int timePerUnit = GsonHelper.getAsInt(json, "time");
			FluidStack input = RecipeSerializers.deserializeFluid(GsonHelper.getAsJsonObject(json, "input"));
			FluidStack output = RecipeSerializers.deserializeFluid(GsonHelper.getAsJsonObject(json, "output"));

			return new StillRecipe(recipeId, timePerUnit, input, output);
		}

		@Override
		public StillRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			int timePerUnit = buffer.readVarInt();
			FluidStack input = FluidStack.readFromPacket(buffer);
			FluidStack output = FluidStack.readFromPacket(buffer);

			return new StillRecipe(recipeId, timePerUnit, input, output);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, StillRecipe recipe) {
			buffer.writeVarInt(recipe.timePerUnit);
			recipe.input.writeToPacket(buffer);
			recipe.output.writeToPacket(buffer);
		}
	}
}
