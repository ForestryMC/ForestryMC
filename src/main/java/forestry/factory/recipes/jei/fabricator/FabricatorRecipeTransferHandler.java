//package forestry.factory.recipes.jei.fabricator;
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
//import forestry.factory.gui.ContainerFabricator;
//import forestry.factory.network.packets.PacketRecipeTransferRequest;
//
//import mezz.jei.api.gui.IGuiIngredient;
//import mezz.jei.api.gui.IRecipeLayout;
//import mezz.jei.api.recipe.transfer.IRecipeTransferError;
//import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
//
//@OnlyIn(Dist.CLIENT)
//public class FabricatorRecipeTransferHandler implements IRecipeTransferHandler<ContainerFabricator> {
//
//	@Override
//	public Class<ContainerFabricator> getContainerClass() {
//		return ContainerFabricator.class;
//	}
//
//	@Override
//	public IRecipeTransferError transferRecipe(ContainerFabricator container, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
//		if (doTransfer) {
//			IInventory craftingInventory = container.getFabricator().getCraftingInventory();
//			NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
//			for (Entry<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredientEntry : recipeLayout.getItemStacks().getGuiIngredients().entrySet()) {
//				IGuiIngredient<ItemStack> guiIngredient = guiIngredientEntry.getValue();
//				int index = guiIngredientEntry.getKey();
//				if (index >= 3 && guiIngredient.getDisplayedIngredient() != null) {
//					ItemStack ingredient = guiIngredient.getDisplayedIngredient().copy();
//					craftingInventory.setInventorySlotContents(index - 3, ingredient);
//					items.set(index - 3, ingredient);
//				}
//			}
//			NetworkUtil.sendToServer(new PacketRecipeTransferRequest(container.getFabricator(), items));
//		}
//		return null;
//	}
//
//}
