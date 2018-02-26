package forestry.sorting.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import forestry.core.gui.ContainerTile;
import forestry.sorting.network.packets.PacketGuiFilterUpdate;
import forestry.sorting.tiles.IFilterContainer;

public class ContainerGeneticFilter extends ContainerTile<TileEntity> {
	private final IFilterContainer container;
	private boolean guiNeedsUpdate = true;

	public ContainerGeneticFilter(IFilterContainer container, InventoryPlayer playerInventory) {
		super(container.getTileEntity());
		this.container = container;
		addInventory(playerInventory, 26, 140);
	}

	protected void addInventory(InventoryPlayer playerInventory, int xInv, int yInv) {
		// Player inventory
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				addSlotToContainer(new SlotGeneticFilter(playerInventory, column + row * 9 + 9, xInv + column * 18, yInv + row * 18));
			}
		}
		// Player hotbar
		for (int column = 0; column < 9; column++) {
			addSlotToContainer(new SlotGeneticFilter(playerInventory, column, xInv + column * 18, yInv + 58));
		}

		IInventory buffer = container.getBuffer();
		if (buffer != null) {
			for (int x = 0; x < 6; x++) {
				addSlotToContainer(new SlotFilterFacing(buffer, x, 8, 18 + x * 18));
			}
		}
	}

	public void setGuiNeedsUpdate(boolean guiNeedsUpdate) {
		this.guiNeedsUpdate = guiNeedsUpdate;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (guiNeedsUpdate) {
			PacketGuiFilterUpdate packet = new PacketGuiFilterUpdate(container);
			sendPacketToListeners(packet);
			guiNeedsUpdate = false;
		}
	}

	public boolean hasSameTile(ContainerGeneticFilter openContainer) {
		return tile == openContainer.tile;
	}
}
