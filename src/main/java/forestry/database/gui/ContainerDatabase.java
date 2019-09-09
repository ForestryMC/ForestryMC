package forestry.database.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import forestry.core.gui.ContainerAnalyzerProvider;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.TileUtil;
import forestry.database.ModuleDatabase;
import forestry.database.tiles.TileDatabase;

public class ContainerDatabase extends ContainerAnalyzerProvider<TileDatabase> {

	public static ContainerDatabase fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
		PacketBufferForestry buf = new PacketBufferForestry(data);
		TileDatabase tile = TileUtil.getTile(inv.player.world, buf.readBlockPos(), TileDatabase.class);
		return new ContainerDatabase(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerDatabase(int windowId, PlayerInventory playerInventory, TileDatabase tileForestry) {
		super(windowId, ModuleDatabase.getContainerTypes().DATABASE, playerInventory, tileForestry, 29, 120);

		addInventory(this, tileForestry);
	}

	private static void addInventory(ContainerForestry container, TileDatabase inventory) {
		//Only to sync the items with the client
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			container.addSlot(new SlotFilteredInventory(inventory, i, -10000, -10000));
		}
	}

	public void sendContainerToListeners() {
		for (IContainerListener listener : listeners) {
			listener.sendAllContents(this, getInventory());
		}
	}

	public IItemHandler getItemHandler() {
		return new InvWrapper(tile.getInternalInventory());
	}
}
