package forestry.core.data.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class SqueezerRecipeBuilder {

	private int processingTime;
	private NonNullList<Ingredient> resources;
	private FluidStack fluidOutput;
	private ItemStack remnants = ItemStack.EMPTY;
	private float remnantsChance;

	public SqueezerRecipeBuilder setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	public SqueezerRecipeBuilder setResources(NonNullList<Ingredient> resources) {
		this.resources = resources;
		return this;
	}

	public SqueezerRecipeBuilder setFluidOutput(FluidStack fluidOutput) {
		this.fluidOutput = fluidOutput;
		return this;
	}

	public SqueezerRecipeBuilder setRemnants(ItemStack remnants) {
		this.remnants = remnants;
		return this;
	}

	public SqueezerRecipeBuilder setRemnantsChance(float remnantsChance) {
		this.remnantsChance = remnantsChance;
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, processingTime, resources, fluidOutput, remnants, remnantsChance));
	}

	private static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final int processingTime;
		private final NonNullList<Ingredient> resources;
		private final FluidStack fluidOutput;
		private final ItemStack remnants;
		private final float remnantsChance;

		public Result(ResourceLocation id, int processingTime, NonNullList<Ingredient> resources, FluidStack fluidOutput, ItemStack remnants, float remnantsChance) {
			this.id = id;
			this.processingTime = processingTime;
			this.resources = resources;
			this.fluidOutput = fluidOutput;
			this.remnants = remnants;
			this.remnantsChance = remnantsChance;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.addProperty("time", processingTime);

			JsonArray resources = new JsonArray();

			for (Ingredient resource : this.resources) {
				resources.add(resource.toJson());
			}

			json.add("resources", resources);
			json.add("output", RecipeSerializers.serializeFluid(fluidOutput));
			json.add("remnant", RecipeSerializers.item(remnants));
			json.addProperty("chance", remnantsChance);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return ISqueezerRecipe.Companion.SERIALIZER;
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
