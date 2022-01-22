package forestry.factory.recipes.jei.fabricator;

import javax.annotation.Nullable;
import java.util.Map.Entry;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.utils.NetworkUtil;
import forestry.factory.gui.ContainerFabricator;
import forestry.factory.network.packets.PacketRecipeTransferRequest;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;

@OnlyIn(Dist.CLIENT)
public class FabricatorRecipeTransferHandler implements IRecipeTransferHandler<ContainerFabricator, IFabricatorRecipe> {

	@Override
	public Class<ContainerFabricator> getContainerClass() {
		return ContainerFabricator.class;
	}

	@Override
	public Class<IFabricatorRecipe> getRecipeClass() {
		return IFabricatorRecipe.class;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(ContainerFabricator container, IFabricatorRecipe recipe, IRecipeLayout recipeLayout, Player player, boolean maxTransfer, boolean doTransfer) {
		if (doTransfer) {
			Container craftingInventory = container.getFabricator().getCraftingInventory();
			NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
			for (Entry<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredientEntry : recipeLayout.getItemStacks().getGuiIngredients().entrySet()) {
				IGuiIngredient<ItemStack> guiIngredient = guiIngredientEntry.getValue();
				int index = guiIngredientEntry.getKey();
				if (index >= 3 && guiIngredient.getDisplayedIngredient() != null) {
					ItemStack ingredient = guiIngredient.getDisplayedIngredient().copy();
					craftingInventory.setItem(index - 3, ingredient);
					items.set(index - 3, ingredient);
				}
			}

			NetworkUtil.sendToServer(new PacketRecipeTransferRequest(container.getFabricator(), items));
		}

		return null;
	}
}
