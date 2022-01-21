package forestry.database.tiles;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import forestry.core.tiles.TileBase;
import forestry.database.features.DatabaseTiles;
import forestry.database.gui.ContainerDatabase;
import forestry.database.inventory.InventoryDatabase;

public class TileDatabase extends TileBase {

	public TileDatabase() {
		super(DatabaseTiles.DATABASE.tileType());
		setInternalInventory(new InventoryDatabase(this));
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
		return new ContainerDatabase(windowId, playerInventory, this);
	}
}
