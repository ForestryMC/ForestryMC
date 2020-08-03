package forestry.sorting.inventory;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.sorting.tiles.TileGeneticFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class InventoryFilter extends InventoryAdapterTile<TileGeneticFilter> {
    public InventoryFilter(TileGeneticFilter tile) {
        super(tile, 6, "Items");
    }

    @Override
    public boolean canExtractItem(int slotIndex, ItemStack stack, Direction side) {
        return false;
    }
}
