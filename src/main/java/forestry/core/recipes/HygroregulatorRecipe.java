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
package forestry.core.recipes;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class HygroregulatorRecipe implements IHygroregulatorRecipe {

	private final ResourceLocation id;
	private final FluidStack liquid;
	private final int transferTime;
	private final float humidChange;
	private final float tempChange;

	public HygroregulatorRecipe(ResourceLocation id, FluidStack liquid, int transferTime, float humidChange, float tempChange) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(liquid);
		Preconditions.checkArgument(transferTime > 0);
		this.id = id;
		this.liquid = liquid;
		this.transferTime = transferTime;
		this.humidChange = humidChange;
		this.tempChange = tempChange;
	}

	@Override
	public FluidStack getResource() {
		return liquid;
	}

	@Override
	public int getTransferTime() {
		return transferTime;
	}

	@Override
	public float getHumidChange() {
		return humidChange;
	}

	@Override
	public float getTempChange() {
		return tempChange;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class Serializer implements RecipeSerializer<HygroregulatorRecipe> {

		@Override
		public HygroregulatorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			FluidStack liquid = RecipeSerializers.deserializeFluid(GsonHelper.getAsJsonObject(json, "liquid"));
			int transferTime = GsonHelper.getAsInt(json, "time");
			float humidChange = GsonHelper.getAsFloat(json, "humidChange");
			float tempChange = GsonHelper.getAsFloat(json, "tempChange");

			return new HygroregulatorRecipe(recipeId, liquid, transferTime, humidChange, tempChange);
		}

		@Override
		public HygroregulatorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			FluidStack liquid = FluidStack.readFromPacket(buffer);
			int transferTime = buffer.readVarInt();
			float humidChange = buffer.readFloat();
			float tempChange = buffer.readFloat();

			return new HygroregulatorRecipe(recipeId, liquid, transferTime, humidChange, tempChange);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, HygroregulatorRecipe recipe) {
			recipe.liquid.writeToPacket(buffer);
			buffer.writeVarInt(recipe.transferTime);
			buffer.writeFloat(recipe.humidChange);
			buffer.writeFloat(recipe.tempChange);
		}
	}
}
