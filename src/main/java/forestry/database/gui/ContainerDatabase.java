package forestry.database.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import forestry.core.gui.ContainerAnalyzerProvider;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.database.tiles.TileDatabase;

public class ContainerDatabase extends ContainerAnalyzerProvider<TileDatabase> {

	public ContainerDatabase(TileDatabase tileForestry, InventoryPlayer playerInventory) {
		super(tileForestry, playerInventory, 29, 120);

		addInventory(this, tileForestry);
	}

	private static void addInventory(ContainerForestry container, TileDatabase inventory) {
		//Only to sync the items with the client
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			container.addSlotToContainer(new SlotFilteredInventory(inventory, i, -10000, -10000));
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
