package forestry.book.items;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import forestry.book.BookLoader;
import forestry.book.gui.GuiForesterBook;
import forestry.book.gui.GuiForestryBookCategories;
import forestry.core.items.ItemWithGui;

public class ItemForesterBook extends ItemWithGui {
	public ItemForesterBook() {

	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		openGui(playerIn);

		ItemStack stack = playerIn.getHeldItem(handIn);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public GuiScreen getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return GuiForesterBook.guiScreen != null ? GuiForesterBook.guiScreen : new GuiForestryBookCategories(BookLoader.INSTANCE.loadBook());
	}

	@Nullable
	@Override
	public Container getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return null;
	}
}
