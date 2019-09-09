package forestry.database.network.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.database.gui.ContainerDatabase;

public class PacketInsertItem extends ForestryPacket implements IForestryPacketServer {
	private final boolean single;

	public PacketInsertItem(boolean single) {
		this.single = single;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeBoolean(single);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.INSERT_ITEM;
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, ServerPlayerEntity player) {
			boolean single = data.readBoolean();
			Container container = player.openContainer;
			if (!(container instanceof ContainerDatabase)) {
				return;
			}

			IItemHandler itemHandler = ((ContainerDatabase) container).getItemHandler();
			if (itemHandler == null) {
				return;
			}
			ItemStack playerStack = player.inventory.getItemStack();
			ItemStack itemStack = playerStack.copy();

			if (single) {
				itemStack.setCount(1);
			}
			ItemStack remaining = ItemHandlerHelper.insertItemStacked(itemHandler, itemStack, false);
			if (single && remaining.isEmpty()) {
				playerStack.shrink(1);
				if (playerStack.isEmpty()) {
					player.inventory.setItemStack(ItemStack.EMPTY);
				}
			} else {
				player.inventory.setItemStack(remaining);
			}

			if (container instanceof ContainerDatabase) {
				((ContainerDatabase) container).sendContainerToListeners();
			}
		}
	}
}
