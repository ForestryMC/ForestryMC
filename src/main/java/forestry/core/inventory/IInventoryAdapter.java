package forestry.core.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.INbtWritable;
import forestry.core.tiles.IFilterSlotDelegate;

public interface IInventoryAdapter extends ISidedInventory, IFilterSlotDelegate, INbtWritable {
	void readFromNBT(NBTTagCompound nbt);
}
