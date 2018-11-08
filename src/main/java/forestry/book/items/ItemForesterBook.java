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

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.IForesterBook;
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

	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen getGui(EntityPlayer player, ItemStack heldItem, int data) {
		IForesterBook book = BookLoader.INSTANCE.loadBook();
		GuiForesterBook guiScreen = GuiForesterBook.getGuiScreen();
		if (guiScreen != null && guiScreen.getBook() != book) {
			GuiForesterBook.setGuiScreen(null);
			guiScreen = null;
		}
		return guiScreen != null ? guiScreen : new GuiForestryBookCategories(book);
	}

	@Nullable
	@Override
	public Container getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return null;
	}
}
