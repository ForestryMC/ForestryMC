package forestry.core.inventory;

import net.minecraft.inventory.ISidedInventory;

import forestry.api.core.INBTTagable;
import forestry.core.tiles.IFilterSlotDelegate;

public interface IInventoryAdapter extends ISidedInventory, IFilterSlotDelegate, INBTTagable {
}
