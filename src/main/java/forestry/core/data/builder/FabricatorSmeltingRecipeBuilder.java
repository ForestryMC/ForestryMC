package forestry.core.data.builder;

import com.google.gson.JsonObject;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.factory.recipes.RecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class FabricatorSmeltingRecipeBuilder {

    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private int meltingPoint;
    private ItemStack resource;
    private FluidStack product;

    private static IllegalStateException error(ResourceLocation id, String message) {
        return new IllegalStateException(message + " (" + id + ")");
    }

    public FabricatorSmeltingRecipeBuilder packagingTime(int meltingPoint) {
        this.meltingPoint = meltingPoint;
        return this;
    }

    public FabricatorSmeltingRecipeBuilder product(FluidStack product) {
        this.product = product;
        return this;
    }

    public FabricatorSmeltingRecipeBuilder product(Fluid product) {
        this.product = new FluidStack(product, 1000);
        return this;
    }

    public FabricatorSmeltingRecipeBuilder resource(ItemStack resource) {
        this.resource = resource;
        return this;
    }

    public FabricatorSmeltingRecipeBuilder resource(IItemProvider provider) {
        this.resource = new ItemStack(provider.asItem());
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        build(consumer, product.getFluid().getRegistryName());
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        validate(id);

        advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                          .withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
                          .withRewards(AdvancementRewards.Builder.recipe(id))
                          .withRequirementsStrategy(IRequirementsStrategy.OR);
        consumer.accept(new Result(
                id,
                meltingPoint,
                resource,
                product,
                advancementBuilder,
                new ResourceLocation(
                        id.getNamespace(),
                        "recipes/" + resource.getItem().getGroup().getPath() + "/" + id.getPath()
                )
        ));
    }

    private void validate(ResourceLocation id) {
        if (meltingPoint <= 0) {
            throw error(id, "Packaging time was not set or is below 1");
        }

        if (product == null || product.isEmpty()) {
            throw error(id, "Liquid was not set");
        }

        if (resource == null || resource.isEmpty()) {
            throw error(id, "Box was not set");
        }
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final int meltingPoint;
        private final ItemStack resource;
        private final FluidStack product;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;

        public Result(
                ResourceLocation id,
                int meltingPoint,
                ItemStack resource,
                FluidStack product,
                Advancement.Builder advancementBuilder,
                ResourceLocation advancementId
        ) {
            this.id = id;
            this.meltingPoint = meltingPoint;
            this.resource = resource;
            this.product = product;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("melting", meltingPoint);
            json.add("resource", RecipeSerializers.item(resource));
            json.add("liquid", RecipeSerializers.serializeFluid(product));
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
            return advancementBuilder.serialize();
        }

        @Override
        public ResourceLocation getAdvancementID() {
            return advancementId;
        }
    }
}
