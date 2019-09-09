package forestry.database.tiles;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

import forestry.core.tiles.TileBase;
import forestry.database.ModuleDatabase;
import forestry.database.gui.ContainerDatabase;
import forestry.database.inventory.InventoryDatabase;

public class TileDatabase extends TileBase {

	public TileDatabase() {
		super(ModuleDatabase.getTiles().DATABASE);
		setInternalInventory(new InventoryDatabase(this));
	}

	@Nullable
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new ContainerDatabase(windowId, playerInventory, this);
	}
}
