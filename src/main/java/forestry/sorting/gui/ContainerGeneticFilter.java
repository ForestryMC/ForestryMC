package forestry.sorting.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.network.FriendlyByteBuf;

import forestry.core.gui.ContainerTile;
import forestry.core.tiles.TileUtil;
import forestry.sorting.features.SortingContainers;
import forestry.sorting.network.packets.PacketGuiFilterUpdate;
import forestry.sorting.tiles.IFilterContainer;
import forestry.sorting.tiles.TileGeneticFilter;

public class ContainerGeneticFilter extends ContainerTile<TileGeneticFilter> {
	private final IFilterContainer container;
	private boolean guiNeedsUpdate = true;

	public static ContainerGeneticFilter fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
		TileGeneticFilter tile = TileUtil.getTile(inv.player.level, data.readBlockPos(), TileGeneticFilter.class);
		return new ContainerGeneticFilter(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerGeneticFilter(int windowId, Inventory playerInventory, IFilterContainer container) {
		super(windowId, SortingContainers.GENETIC_FILTER.containerType(), container.getTileEntity());
		this.container = container;
		addInventory(playerInventory, 26, 140);
	}

	protected void addInventory(Inventory playerInventory, int xInv, int yInv) {
		// Player inventory
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				addSlot(new SlotGeneticFilter(playerInventory, column + row * 9 + 9, xInv + column * 18, yInv + row * 18));
			}
		}
		// Player hotbar
		for (int column = 0; column < 9; column++) {
			addSlot(new SlotGeneticFilter(playerInventory, column, xInv + column * 18, yInv + 58));
		}

		Container buffer = container.getBuffer();
		if (buffer != null) {
			for (int x = 0; x < 6; x++) {
				addSlot(new SlotFilterFacing(buffer, x, 8, 18 + x * 18));
			}
		}
	}

	public void setGuiNeedsUpdate(boolean guiNeedsUpdate) {
		this.guiNeedsUpdate = guiNeedsUpdate;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
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
