package forestry.core.inventory;

import net.minecraft.world.WorldlyContainer;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.tiles.IFilterSlotDelegate;

public interface IInventoryAdapter extends WorldlyContainer, IFilterSlotDelegate, INbtWritable, INbtReadable {

}
