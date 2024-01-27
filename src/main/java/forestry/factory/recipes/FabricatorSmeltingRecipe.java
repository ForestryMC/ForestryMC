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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorSmeltingRecipe;

public class FabricatorSmeltingRecipe implements IFabricatorSmeltingRecipe {

	private final ResourceLocation id;
	private final Ingredient resource;
	private final FluidStack product;
	private final int meltingPoint;

	public FabricatorSmeltingRecipe(ResourceLocation id, Ingredient resource, FluidStack molten, int meltingPoint) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(resource);
		Preconditions.checkArgument(!resource.isEmpty());
		Preconditions.checkNotNull(molten);

		this.id = id;
		this.resource = resource;
		this.product = molten;
		this.meltingPoint = meltingPoint;
	}

	@Override
	public Ingredient getResource() {
		return resource;
	}

	@Override
	public FluidStack getProduct() {
		return product;
	}

	@Override
	public int getMeltingPoint() {
		return meltingPoint;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class Serializer implements RecipeSerializer<FabricatorSmeltingRecipe> {

		@Override
		public FabricatorSmeltingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient resource = RecipeSerializers.deserialize(json.get("resource"));
			FluidStack product = RecipeSerializers.deserializeFluid(GsonHelper.getAsJsonObject(json, "product"));
			int meltingPoint = GsonHelper.getAsInt(json, "melting");

			return new FabricatorSmeltingRecipe(recipeId, resource, product, meltingPoint);
		}

		@Override
		public FabricatorSmeltingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient resource = Ingredient.fromNetwork(buffer);
			FluidStack product = FluidStack.readFromPacket(buffer);
			int meltingPoint = buffer.readVarInt();

			return new FabricatorSmeltingRecipe(recipeId, resource, product, meltingPoint);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, FabricatorSmeltingRecipe recipe) {
			recipe.resource.toNetwork(buffer);
			recipe.product.writeToPacket(buffer);
			buffer.writeVarInt(recipe.meltingPoint);
		}
	}
}
