package forestry.factory.recipes.jei.carpenter;

import java.util.Map.Entry;

import forestry.core.proxy.Proxies;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.gui.ContainerCarpenter;
import forestry.factory.network.packets.PacketRecipeTransferRequest;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CarpenterRecipeTransferHandler implements IRecipeTransferHandler<ContainerCarpenter> {

	@Override
	public Class<ContainerCarpenter> getContainerClass() {
		return ContainerCarpenter.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return ForestryRecipeCategoryUid.CARPENTER;
	}

	@Override
	public IRecipeTransferError transferRecipe(ContainerCarpenter container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		if(doTransfer){
			IInventory craftingInventory = container.getCarpenter().getCraftingInventory();
			ItemStack[] items = new ItemStack[9];
			for(Entry<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredientEntry : recipeLayout.getItemStacks().getGuiIngredients().entrySet()){
				IGuiIngredient<ItemStack> guiIngredient = guiIngredientEntry.getValue();
				if(guiIngredientEntry != null && guiIngredient.getDisplayedIngredient() != null){
					int index = guiIngredientEntry.getKey();
					if (index >= 2) {
	                    ItemStack ingredient = guiIngredient.getDisplayedIngredient().copy();
	                    craftingInventory.setInventorySlotContents(index - 2, ingredient);
	                    items[index - 2] = ingredient;
	                }
				}
			}
			Proxies.net.sendToServer(new PacketRecipeTransferRequest(container.getCarpenter(), items));
		}
		return null;
	}

}
