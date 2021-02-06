package forestry.core.data.builder;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFermenterRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class FermenterRecipeBuilder {

	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
	private Ingredient resource;
	private int fermentationValue;
	private float modifier;
	private Fluid output;
	private FluidStack fluidResource;

	public FermenterRecipeBuilder resource(Ingredient resource) {
		this.resource = resource;
		return this;
	}

	public FermenterRecipeBuilder fermentationValue(int fermentationValue) {
		this.fermentationValue = fermentationValue;
		return this;
	}

	public FermenterRecipeBuilder modifier(float modifier) {
		this.modifier = modifier;
		return this;
	}

	public FermenterRecipeBuilder output(Fluid output) {
		this.output = output;
		return this;
	}

	public FermenterRecipeBuilder fluidResource(FluidStack fluidResource) {
		this.fluidResource = fluidResource;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
				.withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
				.withRewards(AdvancementRewards.Builder.recipe(id))
				.withRequirementsStrategy(IRequirementsStrategy.OR);
		consumer.accept(new Result(id, resource, fermentationValue, modifier, output, fluidResource, advancementBuilder, null));
	}

	private static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final Ingredient resource;
		private final int fermentationValue;
		private final float modifier;
		private final Fluid output;
		private final FluidStack fluidResource;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation id, Ingredient resource, int fermentationValue, float modifier, Fluid output, FluidStack fluidResource, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
			this.id = id;
			this.resource = resource;
			this.fermentationValue = fermentationValue;
			this.modifier = modifier;
			this.output = output;
			this.fluidResource = fluidResource;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
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
			return advancementBuilder.serialize();
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementID() {
			return advancementId;
		}
	}
}
