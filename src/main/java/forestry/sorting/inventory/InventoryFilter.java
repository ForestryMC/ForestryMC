package forestry.sorting.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.sorting.tiles.TileGeneticFilter;

public class InventoryFilter extends InventoryAdapterTile<TileGeneticFilter> {
	public InventoryFilter(TileGeneticFilter tile) {
		super(tile, 6, "Items");
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, EnumFacing side) {
		return false;
	}
}
