package forestry.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class RecipeUtils {
    private RecipeUtils() {
    }

    @Nullable
    public static RecipeManager getRecipeManager(@Nullable World world){
        RecipeManager manager = DistExecutor.safeCallWhenOn(Dist.CLIENT, ()->ClientUtils::getRecipeManager);
        return manager != null ?  manager : world != null ? world.getRecipeManager() : null;
    }

    @Nullable
    public static <C extends IInventory, T extends IRecipe<C>> IRecipe<C> getRecipe(IRecipeType<T> recipeType, ResourceLocation name, @Nullable World world) {
        RecipeManager manager = getRecipeManager(world);
        if(manager == null){
            return null;
        }
        return manager.getRecipes(recipeType).get(name);
    }

    public static <C extends IInventory, T extends IRecipe<C>> List<T> getRecipes(IRecipeType<T> recipeType, C inventory, @Nullable World world) {
        RecipeManager manager = getRecipeManager(world);
        if(manager == null || world == null){
            return Collections.emptyList();
        }
        return manager.getRecipes(recipeType, inventory, world);
    }

}
