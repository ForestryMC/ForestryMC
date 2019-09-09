//package forestry.worktable.compat;
//
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.Map;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.IRecipe;
//
//
//import net.minecraftforge.api.distmarker.Dist;
//
//import net.minecraftforge.api.distmarker.OnlyIn;
//import forestry.core.recipes.RecipeUtil;
//import forestry.worktable.gui.ContainerWorktable;
//import forestry.worktable.inventory.CraftingInventoryForestry;
//import forestry.worktable.recipes.MemorizedRecipe;
//
//import mezz.jei.api.gui.IGuiIngredient;
//import mezz.jei.api.gui.IRecipeLayout;
//import mezz.jei.api.recipe.transfer.IRecipeTransferError;
//import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
//
//@OnlyIn(Dist.CLIENT)
//class WorktableRecipeTransferHandler implements IRecipeTransferHandler<ContainerWorktable> {
//	@Override
//	public Class<ContainerWorktable> getContainerClass() {
//		return ContainerWorktable.class;
//	}
////TODO JEI
//	@Nullable
//	@Override
//	public IRecipeTransferError transferRecipe(ContainerWorktable container, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
//		if (doTransfer) {
//			Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = recipeLayout.getItemStacks().getGuiIngredients();
//
//			CraftingInventoryForestry inventory = new CraftingInventoryForestry(container);
//
//			for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : guiIngredients.entrySet()) {
//				int recipeSlot = entry.getKey();
//				List<ItemStack> allIngredients = entry.getValue().getAllIngredients();
//				if (!allIngredients.isEmpty()) {
//					if (recipeSlot != 0) { // skip the output slot
//						ItemStack firstIngredient = allIngredients.get(0);
//						inventory.setInventorySlotContents(recipeSlot - 1, firstIngredient);
//					}
//				}
//			}
//
//			List<IRecipe> matchingRecipes = RecipeUtil.findMatchingRecipes(inventory, player.world);
//			if (!matchingRecipes.isEmpty()) {
//				MemorizedRecipe recipe = new MemorizedRecipe(inventory, matchingRecipes);
//				container.sendWorktableRecipeRequest(recipe);
//			}
//		}
//
//		return null;
//	}
//}
