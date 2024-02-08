package forestry.energy;

import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.energy.compat.EnergyStorageWrapper;

public class EnergyManager extends EnergyStorage implements IStreamable, INbtReadable, INbtWritable {
	private boolean canExtract = true;

	public EnergyManager(int maxTransfer, int capacity) {
		super(EnergyHelper.scaleForDifficulty(capacity), EnergyHelper.scaleForDifficulty(maxTransfer), EnergyHelper.scaleForDifficulty(maxTransfer));
	}

	public void setReceiveOnly() {
		canExtract = false;
	}

	@Override
	public void read(CompoundTag nbt) {
		setEnergyStored(nbt.getInt("Energy"));
	}

	@Override
	public CompoundTag write(CompoundTag nbt) {
		nbt.putInt("Energy", energy);
		return nbt;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeVarInt(this.energy);
	}

	@Override
	public void readData(PacketBufferForestry data) {
		int energyStored = data.readVarInt();
		setEnergyStored(energyStored);
	}

	public int getMaxEnergyReceived() {
		return this.maxReceive;
	}

	/**
	 * Drains an amount of energy, due to decay from lack of work or other factors
	 */
	public void drainEnergy(int amount) {
		setEnergyStored(energy - amount);
	}

	public void setEnergyStored(int energyStored) {
		this.energy = energyStored;
		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
	}

	public <T> LazyOptional<T> getCapability(Capability<T> capability) {
		if (capability == ForgeCapabilities.ENERGY) {
            IEnergyStorage energyStorage = new EnergyStorageWrapper(this, canExtract);
            return LazyOptional.of(() -> energyStorage).cast();
        }

		return LazyOptional.empty();
	}
}
