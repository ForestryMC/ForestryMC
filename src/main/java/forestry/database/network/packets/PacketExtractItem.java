package forestry.database.network.packets;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.database.gui.ContainerDatabase;

public class PacketExtractItem extends ForestryPacket implements IForestryPacketServer {
	public static final int HALF = 1;
	public static final int SHIFT = 2;
	public static final int CLONE = 4;

	private final int invIndex;
	private final byte flags;

	public PacketExtractItem(int invIndex, byte flags) {
		this.invIndex = invIndex;
		this.flags = flags;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeInt(invIndex);
		data.writeByte(flags);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.EXTRACT_ITEM;
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) {
			int invIndex = data.readInt();
			byte flags = data.readByte();

			if (!player.inventory.getItemStack().isEmpty()) {
				return;
			}

			Container container = player.openContainer;
			if (!(container instanceof ContainerDatabase)) {
				return;
			}

			IItemHandler itemHandler = ((ContainerDatabase) container).getItemHandler();
			if (itemHandler == null) {
				return;
			}

			//Get the item on that position
			ItemStack itemStack = itemHandler.extractItem(invIndex, 64, true);
			//Test if we there is an item on this position
			if (itemStack.isEmpty()) {
				return;
			}
			//Get the max count of this stack
			int maxItemCount = itemStack.getItem().getItemStackLimit(itemStack.copy());
			//Get the count of the stack
			int itemCount = itemStack.getCount();

			if ((flags & CLONE) == CLONE) {
				//Clone the item with the maximal count
				ItemStack extracted = itemStack.copy();
				extracted.setCount(maxItemCount);
				player.inventory.setItemStack(extracted);

				if (container instanceof ContainerDatabase) {
					((ContainerDatabase) container).sendContainerToListeners();
				}
				return;
			}

			int count = 64;
			if ((flags & HALF) == HALF && itemCount > 1) {
				count = itemCount / 2;
			}

			count = Math.min(count, maxItemCount);

			//Simulate an item extraction
			ItemStack extracted = itemHandler.extractItem(invIndex, count, true);
			if (!extracted.isEmpty()) {
				if ((flags & SHIFT) == SHIFT) {
					IItemHandler playerInv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					//Test if the player has enough space
					ItemStack remaining = ItemHandlerHelper.insertItem(playerInv, extracted, true);
					if (remaining.isEmpty()) {
						//Extract the item
						extracted = itemHandler.extractItem(invIndex, count, false);

						//Give the item to the player into the first valid slot
						ItemHandlerHelper.insertItem(playerInv, extracted, false);
					}
				} else {
					//Extract the item
					extracted = itemHandler.extractItem(invIndex, count, false);

					player.inventory.setItemStack(extracted);

					player.updateHeldItem();
				}

				if (container instanceof ContainerDatabase) {
					((ContainerDatabase) container).sendContainerToListeners();
				}
			}
		}
	}
}
