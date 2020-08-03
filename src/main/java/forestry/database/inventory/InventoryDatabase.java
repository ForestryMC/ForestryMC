package forestry.database.inventory;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.utils.GeneticsUtil;
import forestry.database.tiles.TileDatabase;
import genetics.utils.RootUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class InventoryDatabase extends InventoryAdapterTile<TileDatabase> {
    public InventoryDatabase(TileDatabase tile) {
        super(tile, 136, "Items");
    }

    @Override
    public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
        itemStack = GeneticsUtil.convertToGeneticEquivalent(itemStack);
        return RootUtils.getRoot(itemStack).isPresent();
    }

    @Override
    public boolean canExtractItem(int slotIndex, ItemStack stack, Direction side) {
        return super.canExtractItem(slotIndex, stack, side);
    }
}
