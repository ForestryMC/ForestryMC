package forestry.database.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
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
		if (speciesRoot == null) {
			return false;
		}

		IIndividual individual = speciesRoot.getMember(itemStack);
		return individual != null;
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack itemStack) {
		itemStack = GeneticsUtil.convertToGeneticEquivalent(itemStack);
		super.setInventorySlotContents(slotId, itemStack);
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack stack, EnumFacing side) {
		return true;
	}
}
