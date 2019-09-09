package forestry.core.capabilities;

import javax.annotation.Nullable;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;

import net.minecraftforge.common.capabilities.Capability;

public class NullStorage<T> implements Capability.IStorage<T> {
	@Override
	@Nullable
	public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
		return null;
	}

	@Override
	public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {

	}
}
