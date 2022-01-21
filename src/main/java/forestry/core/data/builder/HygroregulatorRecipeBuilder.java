package forestry.core.data.builder;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class HygroregulatorRecipeBuilder {

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

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, liquid, transferTime, humidChange, tempChange));
	}

	private static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final FluidStack liquid;
		private final int transferTime;
		private final float humidChange;
		private final float tempChange;

		public Result(ResourceLocation id, FluidStack liquid, int transferTime, float humidChange, float tempChange) {
			this.id = id;
			this.liquid = liquid;
			this.transferTime = transferTime;
			this.humidChange = humidChange;
			this.tempChange = tempChange;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("liquid", RecipeSerializers.serializeFluid(liquid));
			json.addProperty("time", transferTime);
			json.addProperty("humidChange", humidChange);
			json.addProperty("tempChange", tempChange);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return IHygroregulatorRecipe.Companion.SERIALIZER;
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
