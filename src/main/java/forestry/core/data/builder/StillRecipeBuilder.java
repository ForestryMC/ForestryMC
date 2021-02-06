package forestry.core.data.builder;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IStillRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class StillRecipeBuilder {

	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
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

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
				.withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
				.withRewards(AdvancementRewards.Builder.recipe(id))
				.withRequirementsStrategy(IRequirementsStrategy.OR);
		consumer.accept(new Result(id, timePerUnit, input, output, advancementBuilder, null));
	}

	private static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final int timePerUnit;
		private final FluidStack input;
		private final FluidStack output;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation id, int timePerUnit, FluidStack input, FluidStack output, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
			this.id = id;
			this.timePerUnit = timePerUnit;
			this.input = input;
			this.output = output;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
		}

		@Override
		public void serialize(JsonObject json) {
			json.addProperty("time", timePerUnit);
			json.add("input", RecipeSerializers.serializeFluid(input));
			json.add("output", RecipeSerializers.serializeFluid(output));
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return IStillRecipe.Companion.SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject getAdvancementJson() {
			return advancementBuilder.serialize();
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementID() {
			return advancementId;
		}
	}
}
