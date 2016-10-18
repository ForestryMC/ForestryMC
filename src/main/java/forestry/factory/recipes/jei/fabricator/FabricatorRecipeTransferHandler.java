package forestry.factory.recipes.jei.fabricator;

import java.util.Map.Entry;

import forestry.core.proxy.Proxies;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.gui.ContainerFabricator;
import forestry.factory.network.packets.PacketRecipeTransferRequest;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class FabricatorRecipeTransferHandler implements IRecipeTransferHandler<ContainerFabricator> {

	@Override
	public Class<ContainerFabricator> getContainerClass() {
		return ContainerFabricator.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.FABRICATOR;
	}

	@Override
	public IRecipeTransferError transferRecipe(ContainerFabricator container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		if(doTransfer){
			IInventory craftingInventory = container.getFabricator().getCraftingInventory();
			ItemStack[] items = new ItemStack[9];
			for(Entry<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredientEntry : recipeLayout.getItemStacks().getGuiIngredients().entrySet()){
				IGuiIngredient<ItemStack> guiIngredient = guiIngredientEntry.getValue();
				int index = guiIngredientEntry.getKey();
                if (index >= 3 && guiIngredientEntry != null && guiIngredient.getDisplayedIngredient() != null) {
                    ItemStack ingredient =guiIngredient.getDisplayedIngredient().copy();
                    craftingInventory.setInventorySlotContents(index - 3, ingredient);
                    items[index - 3] = ingredient;
                }
			}
			Proxies.net.sendToServer(new PacketRecipeTransferRequest(container.getFabricator(), items));
		}
		return null;
	}

}
