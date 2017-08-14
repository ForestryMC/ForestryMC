package forestry.factory.tiles;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import forestry.factory.inventory.InventoryCraftingForestry;

public interface ICrafterWorktable {
	ItemStack getResult(InventoryCrafting inventoryCrafting, World world);

	boolean canTakeStack(int slotIndex);

	boolean onCraftingStart(EntityPlayer player);

	void onCraftingComplete(EntityPlayer player);
	
	@Nullable
	IRecipe getRecipeUsed();
}
