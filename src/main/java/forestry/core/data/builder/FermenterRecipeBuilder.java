package forestry.core.data.builder;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFermenterRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class FermenterRecipeBuilder {

	private Ingredient resource;
	private int fermentationValue;
	private float modifier;
	private Fluid output;
	private FluidStack fluidResource;

	public FermenterRecipeBuilder setResource(Ingredient resource) {
		this.resource = resource;
		return this;
	}

	public FermenterRecipeBuilder setFermentationValue(int fermentationValue) {
		this.fermentationValue = fermentationValue;
		return this;
	}

	public FermenterRecipeBuilder setModifier(float modifier) {
		this.modifier = modifier;
		return this;
	}

	public FermenterRecipeBuilder setOutput(Fluid output) {
		this.output = output;
		return this;
	}

	public FermenterRecipeBuilder setFluidResource(FluidStack fluidResource) {
		this.fluidResource = fluidResource;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, resource, fermentationValue, modifier, output, fluidResource));
	}

	private static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final Ingredient resource;
		private final int fermentationValue;
		private final float modifier;
		private final Fluid output;
		private final FluidStack fluidResource;

		public Result(ResourceLocation id, Ingredient resource, int fermentationValue, float modifier, Fluid output, FluidStack fluidResource) {
			this.id = id;
			this.resource = resource;
			this.fermentationValue = fermentationValue;
			this.modifier = modifier;
			this.output = output;
			this.fluidResource = fluidResource;
		}

		@Override
		public void serialize(JsonObject json) {
			json.add("resource", resource.serialize());
			json.addProperty("fermentationValue", fermentationValue);
			json.addProperty("modifier", modifier);
			json.addProperty("output", output.getRegistryName().toString());
			json.add("fluidResource", RecipeSerializers.serializeFluid(fluidResource));
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return IFermenterRecipe.Companion.SERIALIZER;
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
