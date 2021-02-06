package forestry.core.data.builder;

import com.google.gson.JsonObject;

import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.factory.recipes.RecipeSerializers;

public class FabricatorSmeltingRecipeBuilder {

	private int meltingPoint;
	private ItemStack resource;
	private FluidStack product;

	public FabricatorSmeltingRecipeBuilder setMeltingPoint(int meltingPoint) {
		this.meltingPoint = meltingPoint;
		return this;
	}

	public FabricatorSmeltingRecipeBuilder setResource(ItemStack resource) {
		this.resource = resource;
		return this;
	}

	public FabricatorSmeltingRecipeBuilder setProduct(FluidStack product) {
		this.product = product;
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(id, meltingPoint, resource, product));
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final int meltingPoint;
		private final ItemStack resource;
		private final FluidStack product;

		public Result(ResourceLocation id, int meltingPoint, ItemStack resource, FluidStack product) {
			this.id = id;
			this.meltingPoint = meltingPoint;
			this.resource = resource;
			this.product = product;
		}

		@Override
		public void serialize(JsonObject json) {
			json.addProperty("melting", meltingPoint);
			json.add("resource", RecipeSerializers.item(resource));
			json.add("product", RecipeSerializers.serializeFluid(product));
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return IFabricatorSmeltingRecipe.Companion.SERIALIZER;
		}

		@Override
		public JsonObject getAdvancementJson() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementID() {
			return null;
		}
	}
}
