package forestry.factory.recipes;

import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IForestryRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AbstractCraftingProvider<T extends IForestryRecipe> implements ICraftingProvider<T> {
    private final IRecipeType<T> type;
    private final Set<T> globalRecipes = new HashSet<>();

    public AbstractCraftingProvider(IRecipeType<T> type) {
        this.type = type;
    }

    @Override
    public boolean addRecipe(T recipe) {
        return globalRecipes.add(recipe);
    }

    @Override
    public Collection<T> getRecipes(RecipeManager manager) {
        Set<T> recipes = new HashSet<>(globalRecipes);

        for (IRecipe<IInventory> recipe : manager.getRecipes(type).values()) {
            recipes.add((T) recipe);
        }

        return recipes;
    }
}