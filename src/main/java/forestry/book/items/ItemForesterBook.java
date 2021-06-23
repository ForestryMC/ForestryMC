package forestry.book.items;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import forestry.book.ModuleBook;
import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemWithGui;
import forestry.core.network.PacketBufferForestry;

public class ItemForesterBook extends ItemWithGui {

	public ItemForesterBook() {
		super(new Item.Properties().tab(ItemGroupForestry.tabForestry));

	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (worldIn.isClientSide) {
			ModuleBook.proxy.bookOpenGui();
		}

		ItemStack stack = playerIn.getItemInHand(handIn);
		return ActionResult.success(stack);
	}

	@Override
	protected void openGui(ServerPlayerEntity PlayerEntity, ItemStack stack) {
		return;    //TODO hopefully use vanilla hacky method instead
	}

	@Override
	protected void writeContainerData(ServerPlayerEntity player, ItemStack stack, PacketBufferForestry buffer) {
	}

	@Nullable
	@Override
	public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
		return null;
	}
}
