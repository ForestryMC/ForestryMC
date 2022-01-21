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

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.registries.ForgeRegistryEntry;

import forestry.api.recipes.IMoistenerRecipe;

public class MoistenerRecipe implements IMoistenerRecipe {

	private final ResourceLocation id;
	private final int timePerItem;
	private final Ingredient resource;
	private final ItemStack product;

	public MoistenerRecipe(ResourceLocation id, Ingredient resource, ItemStack product, int timePerItem) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(resource, "Resource cannot be null");
		Preconditions.checkNotNull(product, "Product cannot be null");

		this.id = id;
		this.timePerItem = timePerItem;
		this.resource = resource;
		this.product = product;
	}

	@Override
	public int getTimePerItem() {
		return timePerItem;
	}

	@Override
	public Ingredient getResource() {
		return resource;
	}

	@Override
	public ItemStack getProduct() {
		return product;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MoistenerRecipe> {

		@Override
		public MoistenerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			int timePerItem = GsonHelper.getAsInt(json, "time");
			Ingredient resource = RecipeSerializers.deserialize(json.get("resource"));
			ItemStack product = RecipeSerializers.item(GsonHelper.getAsJsonObject(json, "product"));

			return new MoistenerRecipe(recipeId, resource, product, timePerItem);
		}

		@Override
		public MoistenerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			int timePerItem = buffer.readVarInt();
			Ingredient resource = Ingredient.fromNetwork(buffer);
			ItemStack product = buffer.readItem();

			return new MoistenerRecipe(recipeId, resource, product, timePerItem);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, MoistenerRecipe recipe) {
			buffer.writeVarInt(recipe.timePerItem);
			recipe.resource.toNetwork(buffer);
			buffer.writeItem(recipe.product);
		}
	}
}
