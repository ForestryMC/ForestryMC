package forestry.api.core;

import net.minecraft.nbt.NBTTagCompound;

public interface INbtReadable {
	void readFromNBT(NBTTagCompound nbt);
}
