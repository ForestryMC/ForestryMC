package forestry.core.capabilities;

import javax.annotation.Nullable;

import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;

import net.minecraftforge.common.capabilities.Capability;

public class NullStorage<T> implements Capability.IStorage<T> {
	@Override
	@Nullable
	public Tag writeNBT(Capability<T> capability, T instance, Direction side) {
		return null;
	}

	@Override
	public void readNBT(Capability<T> capability, T instance, Direction side, Tag nbt) {

	}
}
