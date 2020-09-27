package forestry.core.data.builder;

import com.google.gson.JsonObject;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.factory.recipes.RecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class CarpenterRecipeBuilder {

    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private int packagingTime;
    private FluidStack liquid;
    private ItemStack box;
    private ShapedRecipeBuilder.Result recipe;

    public CarpenterRecipeBuilder packagingTime(int packagingTime) {
        this.packagingTime = packagingTime;
        return this;
    }

    public CarpenterRecipeBuilder liquid(FluidStack liquid) {
        this.liquid = liquid;
        return this;
    }

    public CarpenterRecipeBuilder liquid(Fluid fluid) {
        this.liquid = new FluidStack(fluid, 1000);
        return this;
    }

    public CarpenterRecipeBuilder box(ItemStack box) {
        this.box = box;
        return this;
    }

    public CarpenterRecipeBuilder box(IItemProvider provider) {
        this.box = new ItemStack(provider.asItem());
        return this;
    }

    public CarpenterRecipeBuilder recipe(ShapedRecipeBuilder.Result recipe) {
        this.recipe = recipe;
        return this;
    }

    public CarpenterRecipeBuilder recipe(Consumer<Consumer<IFinishedRecipe>> consumer) {
        Holder<IFinishedRecipe> holder = new Holder<>();
        consumer.accept(holder::set);
        return recipe((ShapedRecipeBuilder.Result) holder.get());
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        build(consumer, recipe.getID());
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        validate(id);

        advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                .withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
                .withRewards(AdvancementRewards.Builder.recipe(id))
                .withRequirementsStrategy(IRequirementsStrategy.OR);
        consumer.accept(new Result(
                id,
                packagingTime,
                liquid,
                box,
                recipe,
                advancementBuilder,
                new ResourceLocation(
                        id.getNamespace(),
                        "recipes/" + recipe.getSerializer()
                                .read(id, recipe.getRecipeJson())
                                .getRecipeOutput()
                                .getItem()
                                .getGroup()
                                .getPath() + "/" + id.getPath()
                )
        ));
    }

    private void validate(ResourceLocation id) {
        if (packagingTime <= 0) {
            throw error(id, "Packaging time was not set or is below 1");
        }

        if (liquid == null || liquid.isEmpty()) {
            throw error(id, "Liquid was not set");
        }

        if (box == null || box.isEmpty()) {
            throw error(id, "Box was not set");
        }

        if (recipe == null) {
            throw error(id, "Recipe was not set");
        }
    }

    private static IllegalStateException error(ResourceLocation id, String message) {
        return new IllegalStateException(message + " (" + id + ")");
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final int packagingTime;
        private final FluidStack liquid;
        private final ItemStack box;
        private final ShapedRecipeBuilder.Result recipe;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;

        public Result(
                ResourceLocation id,
                int packagingTime,
                FluidStack liquid,
                ItemStack box,
                ShapedRecipeBuilder.Result recipe,
                Advancement.Builder advancementBuilder,
                ResourceLocation advancementId
        ) {
            this.id = id;
            this.packagingTime = packagingTime;
            this.liquid = liquid;
            this.box = box;
            this.recipe = recipe;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("time", packagingTime);
            json.add("liquid", RecipeSerializers.serializeFluid(liquid));
            json.add("box", RecipeSerializers.item(box));
            json.add("recipe", recipe.getRecipeJson());
        }

        @Override
        public ResourceLocation getID() {
            return id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return ICarpenterRecipe.Companion.SERIALIZER;
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

    private static class Holder<T> {
        private T object;

        public T get() {
            return object;
        }

        public void set(T object) {
            this.object = object;
        }
    }
}
