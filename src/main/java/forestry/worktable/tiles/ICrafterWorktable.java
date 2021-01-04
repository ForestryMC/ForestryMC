package forestry.worktable.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ICrafterWorktable {
    ItemStack getResult(CraftingInventory CraftingInventory, World world);

    boolean canTakeStack(int slotIndex);

    boolean onCraftingStart(PlayerEntity player);

    void onCraftingComplete(PlayerEntity player);

    @Nullable
    IRecipe getRecipeUsed();
}
