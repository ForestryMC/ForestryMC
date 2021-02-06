package forestry.core.data.builder;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import forestry.api.recipes.ISqueezerContainerRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class SqueezerContainerRecipeBuilder {

	private ItemStack emptyContainer;
	private int processingTime;
	private ItemStack remnants;
	private float remnantsChance;

	public SqueezerContainerRecipeBuilder setEmptyContainer(ItemStack emptyContainer) {
		this.emptyContainer = emptyContainer;
		return this;
	}

	public SqueezerContainerRecipeBuilder setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	public SqueezerContainerRecipeBuilder setRemnants(ItemStack remnants) {
		this.remnants = remnants;
		return this;
	}

	public SqueezerContainerRecipeBuilder setRemnantsChance(float remnantsChance) {
		this.remnantsChance = remnantsChance;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, emptyContainer, processingTime, remnants, remnantsChance));
	}

	private static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final ItemStack emptyContainer;
		private final int processingTime;
		private final ItemStack remnants;
		private final float remnantsChance;

		public Result(ResourceLocation id, ItemStack emptyContainer, int processingTime, ItemStack remnants, float remnantsChance) {
			this.id = id;
			this.emptyContainer = emptyContainer;
			this.processingTime = processingTime;
			this.remnants = remnants;
			this.remnantsChance = remnantsChance;
		}

		@Override
		public void serialize(JsonObject json) {
			json.add("container", RecipeSerializers.item(emptyContainer));
			json.addProperty("time", processingTime);
			json.add("remnants", RecipeSerializers.item(remnants));
			json.addProperty("remnantsChance", remnantsChance);
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return ISqueezerContainerRecipe.Companion.SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject getAdvancementJson() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementID() {
			return null;
		}
	}
}
