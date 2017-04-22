package forestry.factory.recipes.jei;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import forestry.factory.gui.ContainerWorktable;
import forestry.factory.inventory.InventoryCraftingForestry;
import forestry.factory.recipes.MemorizedRecipe;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class WorktableRecipeTransferHandler implements IRecipeTransferHandler<ContainerWorktable> {
	@Override
	public Class<ContainerWorktable> getContainerClass() {
		return ContainerWorktable.class;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(ContainerWorktable container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = recipeLayout.getItemStacks().getGuiIngredients();

		InventoryCraftingForestry inventory = new InventoryCraftingForestry(container);

		NonNullList<ItemStack> recipeOutputs = NonNullList.create();
		for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : guiIngredients.entrySet()) {
			int recipeSlot = entry.getKey();
			List<ItemStack> allIngredients = entry.getValue().getAllIngredients();
			if (!allIngredients.isEmpty()) {
				if (recipeSlot == 0) {
					recipeOutputs.addAll(allIngredients);
				} else {
					ItemStack firstIngredient = allIngredients.get(0);
					inventory.setInventorySlotContents(recipeSlot - 1, firstIngredient);
				}
			}
		}

		if (!recipeOutputs.isEmpty() && doTransfer) {
			MemorizedRecipe recipe = new MemorizedRecipe(inventory, recipeOutputs);
			container.sendWorktableRecipeRequest(recipe);
		}

		return null;
	}
}
