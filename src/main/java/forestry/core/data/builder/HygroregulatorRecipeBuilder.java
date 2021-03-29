package forestry.core.data.builder;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

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

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, liquid, transferTime, humidChange, tempChange));
	}

	private static class Result implements IFinishedRecipe {
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
		public void serialize(JsonObject json) {
			json.add("liquid", RecipeSerializers.serializeFluid(liquid));
			json.addProperty("time", transferTime);
			json.addProperty("humidChange", humidChange);
			json.addProperty("tempChange", tempChange);
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
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementID() {
			return null;
		}
	}
}
