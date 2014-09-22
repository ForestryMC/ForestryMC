package forestry.energy;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import forestry.core.GameMode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyManager implements IEnergyHandler {
	private enum EnergyTransferMode {
		EXTRACT, RECEIVE, BOTH;
	}

	private int energyPerWork;
	private int minAcceptedEnergy;
	protected EnergyStorage energyStorage;
	private EnergyTransferMode mode = EnergyTransferMode.BOTH;

	public EnergyManager(int minAcceptedEnergy, int maxTransfer, int energyPerWork, int capacity) {
		this.energyPerWork = scaleForDifficulty(energyPerWork);
		this.minAcceptedEnergy = scaleForDifficulty(minAcceptedEnergy);
		energyStorage = new EnergyStorage(scaleForDifficulty(capacity), scaleForDifficulty(maxTransfer), scaleForDifficulty(maxTransfer));
	}

	private static int scaleForDifficulty(int energyPerUse) {
		return Math.round(energyPerUse * GameMode.getGameMode().getFloatSetting("energy.demand.modifier"));
	}

	public void setExtractOnly() {
		mode = EnergyTransferMode.EXTRACT;
	}

	public void setReceiveOnly() {
		mode = EnergyTransferMode.RECEIVE;
	}

	private boolean canExtract() {
		switch (mode) {
			case EXTRACT:
			case BOTH:
				return true;
		}
		return false;
	}

	private boolean canReceive() {
		switch (mode) {
			case RECEIVE:
			case BOTH:
				return true;
		}
		return false;
	}

	/* NBT */
	public EnergyManager readFromNBT(NBTTagCompound nbt) {

		NBTTagCompound energyManagerNBT = nbt.getCompoundTag("EnergyManager");
		NBTTagCompound energyStorageNBT = energyManagerNBT.getCompoundTag("EnergyStorage");
		energyStorage.readFromNBT(energyStorageNBT);

		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		NBTTagCompound energyStorageNBT = new NBTTagCompound();
		energyStorage.writeToNBT(energyStorageNBT);

		NBTTagCompound energyManagerNBT = new NBTTagCompound();
		energyManagerNBT.setTag("EnergyStorage", energyStorageNBT);
		nbt.setTag("EnergyManager", energyManagerNBT);

		return nbt;
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (!canReceive() || maxReceive < minAcceptedEnergy)
			return 0;
		return energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (!canExtract())
			return 0;
		return energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	/**
	 * Consumes one work cycle's worth of energy.
	 * @return true if the energy to do work was consumed
	 */
	public boolean consumeEnergyToDoWork() {
		if (energyStorage.getEnergyStored() < energyPerWork)
			return false;
		energyStorage.modifyEnergyStored(-energyPerWork);
		return true;
	}
}
