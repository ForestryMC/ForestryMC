package forestry.book.patchouli.processor;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.*;

public class FabricatorProcessor implements IComponentProcessor {
    protected IFabricatorRecipe recipe;

    @Override
    public void setup(IVariableProvider variables) {
        int index;
        try {
            index = variables.get("index").asNumber().intValue();
        } catch (Exception e) {
            index = 0;
        }

        ItemStack itemStack;
        try {
            itemStack = variables.get("item").as(ItemStack.class);
        } catch (Exception e) {
            itemStack = ItemStack.EMPTY;
        }

        Collection<IFabricatorRecipe> results = RecipeManagers.fabricatorManager.getRecipes(null);
        Iterator<IFabricatorRecipe> iterator = results.iterator();
        List<IFabricatorRecipe> matches = new ArrayList<>();
        while (iterator.hasNext()) {
            IFabricatorRecipe ifr = iterator.next();
            ItemStack result = ifr.getCraftingGridRecipe().getResultItem();
            String resultID = ForgeRegistries.ITEMS.getKey(result.getItem()).toString();
            String targetID = ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString();
            if (resultID.equals(targetID)) {
                matches.add(ifr);
                break;
            }
        }

        this.recipe = matches.get(index);
    }

    @Override
    public IVariable process(String key) {
        if (key.equals("output")) {
            return IVariable.from(this.recipe.getCraftingGridRecipe().getResultItem());
        } else if (key.equals("fluid")) {
            return IVariable.wrap(this.recipe.getLiquid().getFluid().getRegistryName().toString());
        } else if (key.equals("fluidAmount")) {
            return IVariable.wrap(this.recipe.getLiquid().getAmount());
        } else if (key.startsWith("ingredient")) {
            int index = Integer.parseInt(key.substring("ingredient".length()));
            if (index < 1 || index > 9) return IVariable.empty();

            Ingredient ingredient;
            try {
                ingredient = this.recipe.getCraftingGridRecipe().getIngredients().get(index-1);
            } catch (Exception e) {
                ingredient = Ingredient.EMPTY;
            }
            return IVariable.from(ingredient.getItems());
        } else if (key.equals("plan")) {
            return IVariable.from(this.recipe.getPlan());
        } else if (key.equals("metal")) {
            if (this.recipe.getLiquid().getFluid().getRegistryName().getPath().contains("glass")) {
                return IVariable.from(new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation("minecraft:sand")
                )));
            }

            List<ItemStack> results = new ArrayList<>();

            Iterator<IFabricatorSmeltingRecipe> iterator = RecipeManagers.fabricatorSmeltingManager.getRecipes(Minecraft.getInstance().level.getRecipeManager()).iterator();
            while (iterator.hasNext()) {
                IFabricatorSmeltingRecipe r = iterator.next();
                if (r.getProduct().isFluidEqual(this.recipe.getLiquid())) {
                    Collections.addAll(results, r.getResource().getItems());
                }
            }

            return IVariable.from(results.get(0));
        } else {
            return IVariable.empty();
        }
    }
}
