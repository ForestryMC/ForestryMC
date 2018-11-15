package forestry.core.inventory;

import net.minecraft.inventory.ISidedInventory;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.tiles.IFilterSlotDelegate;

public interface IInventoryAdapter extends ISidedInventory, IFilterSlotDelegate, INbtWritable, INbtReadable {

}
