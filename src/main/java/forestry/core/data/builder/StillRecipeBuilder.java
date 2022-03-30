package forestry.core.data.builder;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IStillRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class StillRecipeBuilder {

	private int timePerUnit;
	private FluidStack input;
	private FluidStack output;

	public StillRecipeBuilder setTimePerUnit(int timePerUnit) {
		this.timePerUnit = timePerUnit;
		return this;
	}

	public StillRecipeBuilder setInput(FluidStack input) {
		this.input = input;
		return this;
	}

	public StillRecipeBuilder setOutput(FluidStack output) {
		this.output = output;
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, timePerUnit, input, output));
	}

	private static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final int timePerUnit;
		private final FluidStack input;
		private final FluidStack output;

		public Result(ResourceLocation id, int timePerUnit, FluidStack input, FluidStack output) {
			this.id = id;
			this.timePerUnit = timePerUnit;
			this.input = input;
			this.output = output;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.addProperty("time", timePerUnit);
			json.add("input", RecipeSerializers.serializeFluid(input));
			json.add("output", RecipeSerializers.serializeFluid(output));
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return IStillRecipe.Companion.SERIALIZER;
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
