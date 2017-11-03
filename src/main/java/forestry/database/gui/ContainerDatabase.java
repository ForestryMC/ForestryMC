package forestry.database.gui;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import forestry.core.gui.ContainerForestry;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.database.tiles.TileDatabase;

public class ContainerDatabase extends ContainerTile<TileDatabase> implements IGuiSelectable {

	public static final int SELECT_ID = 0;

	public ContainerDatabase(TileDatabase tileForestry, InventoryPlayer playerInventory) {
		super(tileForestry, playerInventory, 18, 120);

		addInventory(this, tileForestry);
	}

	public static void addInventory(ContainerForestry container, TileDatabase inventory) {
		//Only to sync the items with the client
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			container.addSlotToContainer(new SlotFilteredInventory(inventory, i, -10000, -10000));
		}

		container.addSlotToContainer(new Slot(inventory.analyzerInventory, 0, 184, 120));
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, int primary, int secondary) {
		if(primary == SELECT_ID){
			tile.analyzeSpecimen(secondary);
		}
	}

	public void sendContainerToListeners(){
		for(IContainerListener listener : listeners){
			listener.sendAllContents(this, getInventory());
		}
	}

	public IItemHandler getItemHandler(){
		return new InvWrapper(tile.getInternalInventory());
	}
}
