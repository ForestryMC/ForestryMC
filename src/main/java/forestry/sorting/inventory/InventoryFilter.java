package forestry.sorting.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.sorting.tiles.TileGeneticFilter;

public class InventoryFilter extends InventoryAdapterTile<TileGeneticFilter> {
	public InventoryFilter(TileGeneticFilter tile) {
		super(tile, 6, "Items");
	}

	@Override
	public boolean canTakeItemThroughFace(int slotIndex, ItemStack stack, Direction side) {
		return false;
	}
}
