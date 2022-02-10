package forestry.worktable.compat;

import forestry.core.utils.JeiUtil;
import forestry.core.utils.RecipeUtils;
import forestry.worktable.gui.ContainerWorktable;
import forestry.worktable.inventory.CraftingInventoryForestry;
import forestry.worktable.recipes.MemorizedRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
class WorktableRecipeTransferHandler implements IRecipeTransferHandler<ContainerWorktable, CraftingRecipe> {
	@Override
	public Class<ContainerWorktable> getContainerClass() {
		return ContainerWorktable.class;
	}

	@Override
	public Class<CraftingRecipe> getRecipeClass() {
		return CraftingRecipe.class;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(ContainerWorktable container, CraftingRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
		if (doTransfer) {
			CraftingInventoryForestry inventory = new CraftingInventoryForestry(container);

			NonNullList<ItemStack> firstItemStacks = JeiUtil.getFirstItemStacks(recipeSlots);
			for (int i = 0; i < firstItemStacks.size(); i++) {
				ItemStack firstItemStack = firstItemStacks.get(i);
				inventory.setItem(i, firstItemStack);
			}

			List<CraftingRecipe> matchingRecipes = RecipeUtils.findMatchingRecipes(inventory, player.level);
			if (!matchingRecipes.isEmpty()) {
				MemorizedRecipe memorizedRecipe = new MemorizedRecipe(inventory, matchingRecipes);
				container.sendWorktableRecipeRequest(memorizedRecipe);
			}
		}
		return null;
	}
}
