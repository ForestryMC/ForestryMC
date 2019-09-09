//package forestry.factory.recipes.jei.carpenter;
//
//import java.util.Map.Entry;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.NonNullList;
//
//
//import net.minecraftforge.api.distmarker.Dist;
//
//import net.minecraftforge.api.distmarker.OnlyIn;
//import forestry.core.utils.NetworkUtil;
//import forestry.factory.gui.ContainerCarpenter;
//import forestry.factory.network.packets.PacketRecipeTransferRequest;
//
//import mezz.jei.api.gui.IGuiIngredient;
//import mezz.jei.api.gui.IRecipeLayout;
//import mezz.jei.api.recipe.transfer.IRecipeTransferError;
//import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
//
//@OnlyIn(Dist.CLIENT)
//public class CarpenterRecipeTransferHandler implements IRecipeTransferHandler<ContainerCarpenter> {
//
//	@Override
//	public Class<ContainerCarpenter> getContainerClass() {
//		return ContainerCarpenter.class;
//	}
//
//	@Override
//	public IRecipeTransferError transferRecipe(ContainerCarpenter container, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
//		if (doTransfer) {
//			IInventory craftingInventory = container.getCarpenter().getCraftingInventory();
//			NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
//			for (Entry<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredientEntry : recipeLayout.getItemStacks().getGuiIngredients().entrySet()) {
//				IGuiIngredient<ItemStack> guiIngredient = guiIngredientEntry.getValue();
//				if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
//					int index = guiIngredientEntry.getKey();
//					if (index >= 2) {
//						ItemStack ingredient = guiIngredient.getDisplayedIngredient().copy();
//						craftingInventory.setInventorySlotContents(index - 2, ingredient);
//						items.set(index - 2, ingredient);
//					}
//				}
//			}
//			NetworkUtil.sendToServer(new PacketRecipeTransferRequest(container.getCarpenter(), items));
//		}
//		return null;
//	}
//
//}
