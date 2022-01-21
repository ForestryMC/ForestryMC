package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.fml.DistExecutor;

public final class RecipeUtils {
    private RecipeUtils() {
    }

    @Nullable
    public static RecipeManager getRecipeManager(@Nullable Level world){
        RecipeManager manager = DistExecutor.safeCallWhenOn(Dist.CLIENT, ()->ClientUtils::getRecipeManager);
        return manager != null ?  manager : world != null ? world.getRecipeManager() : null;
    }

    @Nullable
    public static <C extends Container, T extends Recipe<C>> Recipe<C> getRecipe(RecipeType<T> recipeType, ResourceLocation name, @Nullable Level world) {
        RecipeManager manager = getRecipeManager(world);
        if(manager == null){
            return null;
        }
        return manager.byType(recipeType).get(name);
    }

    public static <C extends Container, T extends Recipe<C>> List<T> getRecipes(RecipeType<T> recipeType, C inventory, @Nullable Level world) {
        RecipeManager manager = getRecipeManager(world);
        if (manager == null || world == null) {
            return Collections.emptyList();
        }
		return manager.getRecipesFor(recipeType, inventory, world);
    }

    public static List<CraftingRecipe> findMatchingRecipes(CraftingContainer inventory, Level world) {
		return world.getRecipeManager().getRecipesFor(RecipeType.CRAFTING, inventory, world).stream().filter(recipe -> recipe.matches(inventory, world)).collect(Collectors.toList());
    }

}
