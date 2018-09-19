package forestry.database.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.utils.GeneticsUtil;
import forestry.database.tiles.TileDatabase;

public class InventoryDatabase extends InventoryAdapterTile<TileDatabase> {
	public InventoryDatabase(TileDatabase tile) {
		super(tile, 136, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		itemStack = GeneticsUtil.convertToGeneticEquivalent(itemStack);
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);
		return speciesRoot != null;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, EnumFacing side) {
		return super.canExtractItem(slotIndex, stack, side);
	}
}
