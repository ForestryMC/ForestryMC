package forestry.core.data.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.factory.recipes.RecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class CentrifugeRecipeBuilder {

    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private int processingTime;
    private ItemStack input;
    private final NonNullList<ICentrifugeRecipe.Product> outputs = NonNullList.create();

    public CentrifugeRecipeBuilder processingTime(int processingTime) {
        this.processingTime = processingTime;
        return this;
    }

    public CentrifugeRecipeBuilder input(ItemStack input) {
        this.input = input;
        return this;
    }

    public CentrifugeRecipeBuilder input(IItemProvider provider) {
        this.input = new ItemStack(provider.asItem());
        return this;
    }

    public CentrifugeRecipeBuilder product(ICentrifugeRecipe.Product product) {
        outputs.add(product);
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        build(consumer, input.getItem().getRegistryName());
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        validate(id);

        advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                          .withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
                          .withRewards(AdvancementRewards.Builder.recipe(id))
                          .withRequirementsStrategy(IRequirementsStrategy.OR);
        consumer.accept(new Result(
                id,
                processingTime,
                input,
                outputs,
                advancementBuilder,
                new ResourceLocation(
                        id.getNamespace(),
                        "recipes/" + input.getItem().getGroup().getPath() + "/" + id.getPath()
                )
        ));
    }

    private void validate(ResourceLocation id) {
        if (processingTime <= 0) {
            throw error(id, "Processing time was not set or is below 1");
        }

        if (input == null || input.isEmpty()) {
            throw error(id, "Input was not set");
        }

        if (outputs.isEmpty()) {
            throw error(id, "Recipe outputs were empty");
        }
    }

    private static IllegalStateException error(ResourceLocation id, String message) {
        return new IllegalStateException(message + " (" + id + ")");
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final int processingTime;
        private final ItemStack input;
        private final NonNullList<ICentrifugeRecipe.Product> outputs;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;

        public Result(
                ResourceLocation id,
                int processingTime,
                ItemStack input,
                NonNullList<ICentrifugeRecipe.Product> outputs,
                Advancement.Builder advancementBuilder,
                ResourceLocation advancementId
        ) {
            this.id = id;
            this.processingTime = processingTime;
            this.input = input;
            this.outputs = outputs;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("time", processingTime);
            json.add("input", RecipeSerializers.item(input));

            JsonArray products = new JsonArray();

            for (ICentrifugeRecipe.Product product : outputs) {
                JsonObject object = new JsonObject();
                object.addProperty("chance", product.getProbability());
                object.add("item", RecipeSerializers.item(product.getStack()));
                products.add(object);
            }

            json.add("products", products);
        }

        @Override
        public ResourceLocation getID() {
            return id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return ICentrifugeRecipe.Companion.SERIALIZER;
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
