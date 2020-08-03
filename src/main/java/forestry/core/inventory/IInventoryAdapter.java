package forestry.core.inventory;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.tiles.IFilterSlotDelegate;
import net.minecraft.inventory.ISidedInventory;

public interface IInventoryAdapter extends ISidedInventory, IFilterSlotDelegate, INbtWritable, INbtReadable {

}
