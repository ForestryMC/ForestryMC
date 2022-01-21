package forestry.core.data.builder;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;

import forestry.api.recipes.IMoistenerRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class MoistenerRecipeBuilder {

	private int timePerItem;
	private Ingredient resource;
	private ItemStack product;

	public MoistenerRecipeBuilder setTimePerItem(int timePerItem) {
		this.timePerItem = timePerItem;
		return this;
	}

	public MoistenerRecipeBuilder setResource(Ingredient resource) {
		this.resource = resource;
		return this;
	}

	public MoistenerRecipeBuilder setProduct(ItemStack product) {
		this.product = product;
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, timePerItem, resource, product));
	}

	private static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final int timePerItem;
		private final Ingredient resource;
		private final ItemStack product;

		public Result(ResourceLocation id, int timePerItem, Ingredient resource, ItemStack product) {
			this.id = id;
			this.timePerItem = timePerItem;
			this.resource = resource;
			this.product = product;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.addProperty("time", timePerItem);
			json.add("resource", resource.toJson());
			json.add("product", RecipeSerializers.item(product));
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return IMoistenerRecipe.Companion.SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
