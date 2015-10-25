package forestry.core.inventory;

import net.minecraft.inventory.ISidedInventory;

import forestry.api.core.INBTTagable;
import forestry.core.interfaces.IFilterSlotDelegate;

public interface IInventoryAdapter extends ISidedInventory, IFilterSlotDelegate, INBTTagable {

}
