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

import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class HygroregulatorRecipeBuilder {

	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
	private FluidStack liquid;
	private int transferTime;
	private float humidChange;
	private float tempChange;

	public HygroregulatorRecipeBuilder setLiquid(FluidStack liquid) {
		this.liquid = liquid;
		return this;
	}

	public HygroregulatorRecipeBuilder setTransferTime(int transferTime) {
		this.transferTime = transferTime;
		return this;
	}

	public HygroregulatorRecipeBuilder setHumidChange(float humidChange) {
		this.humidChange = humidChange;
		return this;
	}

	public HygroregulatorRecipeBuilder setTempChange(float tempChange) {
		this.tempChange = tempChange;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
				.withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
				.withRewards(AdvancementRewards.Builder.recipe(id))
				.withRequirementsStrategy(IRequirementsStrategy.OR);
		consumer.accept(new Result(id, liquid, transferTime, humidChange, tempChange, advancementBuilder, null));
	}

	private static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final FluidStack liquid;
		private final int transferTime;
		private final float humidChange;
		private final float tempChange;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation id, FluidStack liquid, int transferTime, float humidChange, float tempChange, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
			this.id = id;
			this.liquid = liquid;
			this.transferTime = transferTime;
			this.humidChange = humidChange;
			this.tempChange = tempChange;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
		}

		@Override
		public void serialize(JsonObject json) {
			json.add("liquid", RecipeSerializers.serializeFluid(liquid));
			json.addProperty("time", transferTime);
			json.addProperty("humidChange", humidChange);
			json.addProperty("tempChance", tempChange);
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return IHygroregulatorRecipe.Companion.SERIALIZER;
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