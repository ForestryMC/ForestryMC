package forestry.database.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import forestry.core.gui.ContainerAnalyzerProvider;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TileUtil;
import forestry.database.features.DatabaseContainers;
import forestry.database.tiles.TileDatabase;

public class ContainerDatabase extends ContainerAnalyzerProvider<TileDatabase> {

	public static ContainerDatabase fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
		PacketBufferForestry buf = new PacketBufferForestry(data);
		TileDatabase tile = TileUtil.getTile(inv.player.level, buf.readBlockPos(), TileDatabase.class);
		return new ContainerDatabase(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerDatabase(int windowId, Inventory playerInventory, TileDatabase tileForestry) {
		super(windowId, DatabaseContainers.DATABASE.containerType(), playerInventory, tileForestry, 29, 120);

		addInventory(this, tileForestry);
	}

	private static void addInventory(ContainerForestry container, TileDatabase inventory) {
		//Only to sync the items with the client
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			container.addSlot(new SlotFilteredInventory(inventory, i, -10000, -10000));
		}
	}

	public void sendContainerToListeners() {
		for (ContainerListener listener : containerListeners) {
			listener.refreshContainer(this, getItems());
		}
	}

	public IItemHandler getItemHandler() {
		return new InvWrapper(tile.getInternalInventory());
	}
}
