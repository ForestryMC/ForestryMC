package forestry.book.patchouli.processor;

import forestry.Forestry;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class CarpenterProcessor implements IComponentProcessor {
    protected ICarpenterRecipe recipe;

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

        /*
        Manually iterate over all the carpenter recipes by result item ResourceLocation id
        because looking up the recipe in carpenterManager doesn't work for some recipes
        (idk why, maybe i was doing it wrong)
        */
        Collection<ICarpenterRecipe> results = RecipeManagers.carpenterManager.getRecipes(null);
        Iterator<ICarpenterRecipe> iterator = results.iterator();
        List<ICarpenterRecipe> matches = new ArrayList<>();
        while (iterator.hasNext()) {
            ICarpenterRecipe icr = iterator.next();
            ItemStack result = icr.getResult();
            String resultID = ForgeRegistries.ITEMS.getKey(result.getItem()).toString();
            String targetID = ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString();
            if (resultID.equals(targetID)) {
                matches.add(icr);
                break;
            }
        }

        this.recipe = matches.get(index);
    }

    @Override
    public IVariable process(String key) {
        if (key.equals("output")) {
            return IVariable.from(this.recipe.getResult());
        } else if (key.equals("fluid")) {
            return IVariable.from(this.recipe.getFluidResource());
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
        } else {
            return IVariable.empty();
        }
    }
}
