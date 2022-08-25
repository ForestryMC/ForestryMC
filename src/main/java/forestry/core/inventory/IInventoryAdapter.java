package forestry.core.inventory;

import forestry.api.core.INBTTagable;
import forestry.core.tiles.IFilterSlotDelegate;
import net.minecraft.inventory.ISidedInventory;

public interface IInventoryAdapter extends ISidedInventory, IFilterSlotDelegate, INBTTagable {}
