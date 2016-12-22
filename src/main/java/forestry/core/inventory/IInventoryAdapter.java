package forestry.core.inventory;

import forestry.api.core.INbtWritable;
import forestry.core.tiles.IFilterSlotDelegate;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;

public interface IInventoryAdapter extends ISidedInventory, IFilterSlotDelegate, INbtWritable {
	void readFromNBT(NBTTagCompound nbt);
}
