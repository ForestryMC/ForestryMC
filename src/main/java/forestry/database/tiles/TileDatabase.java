package forestry.database.tiles;

import forestry.core.tiles.TileBase;
import forestry.database.features.DatabaseTiles;
import forestry.database.gui.ContainerDatabase;
import forestry.database.inventory.InventoryDatabase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nullable;

public class TileDatabase extends TileBase {

    public TileDatabase() {
        super(DatabaseTiles.DATABASE.tileType());
        setInternalInventory(new InventoryDatabase(this));
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ContainerDatabase(windowId, playerInventory, this);
    }
}
