package forestry.book.items;

import forestry.api.book.IForesterBook;
import forestry.book.BookLoader;
import forestry.book.gui.GuiForesterBook;
import forestry.book.gui.GuiForestryBookCategories;
import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemWithGui;
import forestry.core.network.PacketBufferForestry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ItemForesterBook extends ItemWithGui {

    public ItemForesterBook() {
        super(new Item.Properties().group(ItemGroupForestry.tabForestry));

    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        bookOpenGui(playerIn, playerIn.getHeldItem(handIn));

        ItemStack stack = playerIn.getHeldItem(handIn);
        return ActionResult.resultSuccess(stack);
    }

    @Override
    protected void openGui(ServerPlayerEntity PlayerEntity, ItemStack stack) {
        return;    //TODO hopefully use vanilla hacky method instead
    }

    @Override
    protected void writeContainerData(ServerPlayerEntity player, ItemStack stack, PacketBufferForestry buffer) {
    }

    @OnlyIn(Dist.CLIENT)
    private void bookOpenGui(PlayerEntity player, ItemStack stack) {
        IForesterBook book = BookLoader.INSTANCE.loadBook();
        GuiForesterBook guiScreen = GuiForesterBook.getGuiScreen();
        if (guiScreen != null && guiScreen.getBook() != book) {
            GuiForesterBook.setGuiScreen(null);
            guiScreen = null;
        }
        GuiForesterBook bookGui = guiScreen != null ? guiScreen : new GuiForestryBookCategories(book);
        Minecraft.getInstance().displayGuiScreen(bookGui);    //TODO does this work
    }

    @Nullable
    @Override
    public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
        return null;
    }
}
