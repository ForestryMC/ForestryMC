package forestry.energy.compat;

import net.minecraftforge.energy.IEnergyStorage;

import forestry.energy.EnergyManager;

/**
 * Wraps an {@link EnergyManager} to provide restrictions on extraction of receiving of energy.
 */
public class EnergyStorageWrapper implements IEnergyStorage {
	private final EnergyManager energyManager;
	private final boolean canExtract;

	public EnergyStorageWrapper(EnergyManager energyManager, boolean canExtract) {
		this.energyManager = energyManager;
		this.canExtract = canExtract;
	}

	@Override
	public boolean canExtract() {
		return canExtract && energyManager.canExtract();
	}

	@Override
	public boolean canReceive() {
		return energyManager.canReceive();
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
        return energyManager.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (canExtract) {
			return energyManager.extractEnergy(maxExtract, simulate);
		} else {
			return 0;
		}
	}

	@Override
	public int getEnergyStored() {
		return energyManager.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return energyManager.getMaxEnergyStored();
	}
}
