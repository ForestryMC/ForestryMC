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
package forestry.core.data.builder;

import com.google.gson.JsonObject;

import java.util.function.Consumer;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class FabricatorSmeltingRecipeBuilder {

	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
	private int meltingPoint;
	private ItemStack resource;
	private FluidStack product;

	public FabricatorSmeltingRecipeBuilder setMeltingPoint(int meltingPoint) {
		this.meltingPoint = meltingPoint;
		return this;
	}

	public FabricatorSmeltingRecipeBuilder setResource(ItemStack resource) {
		this.resource = resource;
		return this;
	}

	public FabricatorSmeltingRecipeBuilder setProduct(FluidStack product) {
		this.product = product;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
				.withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
				.withRewards(AdvancementRewards.Builder.recipe(id))
				.withRequirementsStrategy(IRequirementsStrategy.OR);
		consumer.accept(new Result(id, meltingPoint, resource, product, advancementBuilder, null));
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final int meltingPoint;
		private final ItemStack resource;
		private final FluidStack product;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation id, int meltingPoint, ItemStack resource, FluidStack product, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
			this.id = id;
			this.meltingPoint = meltingPoint;
			this.resource = resource;
			this.product = product;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
		}

		@Override
		public void serialize(JsonObject json) {
			json.addProperty("melting", meltingPoint);
			json.add("resource", RecipeSerializers.item(resource));
			json.add("product", RecipeSerializers.serializeFluid(product));
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return IFabricatorSmeltingRecipe.Companion.SERIALIZER;
		}

		@Override
		public JsonObject getAdvancementJson() {
			return advancementBuilder.serialize();
		}

		@Override
		public ResourceLocation getAdvancementID() {
			return advancementId;
		}
	}
}
