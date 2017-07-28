package forestry.factory.recipes.jei;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import forestry.core.recipes.RecipeUtil;
import forestry.factory.gui.ContainerWorktable;
import forestry.factory.inventory.InventoryCraftingForestry;
import forestry.factory.recipes.MemorizedRecipe;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

class WorktableRecipeTransferHandler implements IRecipeTransferHandler<ContainerWorktable> {
	@Override
	public Class<ContainerWorktable> getContainerClass() {
		return ContainerWorktable.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(ContainerWorktable container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		if (doTransfer) {
			Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = recipeLayout.getItemStacks().getGuiIngredients();

			InventoryCraftingForestry inventory = new InventoryCraftingForestry(container);
			
			for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : guiIngredients.entrySet()) {
				int recipeSlot = entry.getKey();
				List<ItemStack> allIngredients = entry.getValue().getAllIngredients();
				if (!allIngredients.isEmpty()) {
					if (recipeSlot != 0) { // skip the output slot
						ItemStack firstIngredient = allIngredients.get(0);
						inventory.setInventorySlotContents(recipeSlot - 1, firstIngredient);
					}
				}
			}
			
			List<IRecipe> matchingRecipes = RecipeUtil.findMatchingRecipes(inventory, player.worldObj);
			if (!matchingRecipes.isEmpty()) {
				MemorizedRecipe recipe = new MemorizedRecipe(inventory, matchingRecipes);
				container.sendWorktableRecipeRequest(recipe);
			}
		}

		return null;
	}
}
