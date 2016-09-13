package forestry.energy.compat;

import forestry.energy.EnergyManager;
import forestry.energy.EnergyTransferMode;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Wraps an {@link EnergyManager} to provide restrictions on extraction of receiving of energy.
 */
public class EnergyStorageWrapper implements IEnergyStorage {
	private final EnergyManager energyManager;
	private final EnergyTransferMode mode;

	public EnergyStorageWrapper(EnergyManager energyManager, EnergyTransferMode mode) {
		this.energyManager = energyManager;
		this.mode = mode;
	}

	@Override
	public boolean canExtract() {
		return mode.canExtract() && energyManager.canExtract();
	}

	@Override
	public boolean canReceive() {
		return mode.canReceive() && energyManager.canReceive();
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (mode.canReceive()) {
			return energyManager.receiveEnergy(maxReceive, simulate);
		} else {
			return 0;
		}
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (mode.canExtract()) {
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
