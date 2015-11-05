package forestry.energy;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.core.ForestryAPI;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.tiles.TileEngine;
import forestry.core.utils.BlockUtil;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;

public class EnergyManager implements IEnergyHandler, IStreamable {
	private enum EnergyTransferMode {
		EXTRACT, RECEIVE, BOTH
	}

	private final EnergyStorage energyStorage;
	private EnergyTransferMode mode = EnergyTransferMode.BOTH;

	public EnergyManager(int maxTransfer, int capacity) {
		this.energyStorage = new EnergyStorage(scaleForDifficulty(capacity), scaleForDifficulty(maxTransfer), scaleForDifficulty(maxTransfer));
	}

	public static int scaleForDifficulty(int energyPerUse) {
		return Math.round(energyPerUse * ForestryAPI.activeMode.getFloatSetting("energy.demand.modifier"));
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

	/* Packets */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		int energyStored = energyStorage.getEnergyStored();
		data.writeInt(energyStored);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		int energyStored = data.readInt();
		energyStorage.setEnergyStored(energyStored);
	}

	public int toGuiInt() {
		return energyStorage.getEnergyStored();
	}

	public void fromGuiInt(int packetInt) {
		energyStorage.setEnergyStored(packetInt);
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (!canReceive()) {
			return 0;
		}
		return energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}
		return energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energyStorage.getEnergyStored();
	}

	public int getTotalEnergyStored() {
		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energyStorage.getMaxEnergyStored();
	}

	public int getMaxEnergyStored() {
		return energyStorage.getMaxEnergyStored();
	}

	public int getMaxEnergyReceived() {
		return energyStorage.getMaxReceive();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	/**
	 * Consumes one work cycle's worth of energy.
	 *
	 * @return true if the energy to do work was consumed
	 */
	public boolean consumeEnergyToDoWork(int ticksPerWorkCycle, int energyPerWorkCycle) {
		int energyPerCycle = (int) Math.ceil(energyPerWorkCycle / (float) ticksPerWorkCycle);
		if (energyStorage.getEnergyStored() < energyPerCycle) {
			return false;
		}

		energyStorage.modifyEnergyStored(-energyPerCycle);
		return true;
	}

	/**
	 * @return whether this can send energy to the target tile
	 */
	public boolean canSendEnergy(ForgeDirection orientation, TileEntity tile) {
		return sendEnergy(orientation, tile, Integer.MAX_VALUE, true) > 0;
	}

	/**
	 * Sends as much energy as it can to the tile at orientation.
	 * For power sources. Ignores canExtract()
	 *
	 * @return amount sent
	 */
	public int sendEnergy(ForgeDirection orientation, TileEntity tile) {
		return sendEnergy(orientation, tile, Integer.MAX_VALUE, false);
	}

	/**
	 * Sends amount of energy to the tile at orientation.
	 * For power sources. Ignores canExtract()
	 *
	 * @return amount sent
	 */
	public int sendEnergy(ForgeDirection orientation, TileEntity tile, int amount, boolean simulate) {
		int sent = 0;
		if (BlockUtil.isEnergyReceiverOrEngine(orientation.getOpposite(), tile)) {
			int extractable = energyStorage.extractEnergy(amount, true);
			if (extractable > 0) {

				if (tile instanceof IEnergyReceiver) {
					IEnergyReceiver receptor = (IEnergyReceiver) tile;
					sent = receptor.receiveEnergy(orientation.getOpposite(), extractable, simulate);
				} else if (tile instanceof TileEngine) {
					TileEngine receptor = (TileEngine) tile;
					sent = receptor.getEnergyManager().receiveEnergy(orientation.getOpposite(), extractable, simulate);
				}

				energyStorage.extractEnergy(sent, simulate);
			}
		}
		return sent;
	}

	/**
	 * Drains an amount of energy, due to decay from lack of work or other factors
	 */
	public void drainEnergy(int amount) {
		energyStorage.modifyEnergyStored(-amount);
	}

	/**
	 * Creates an amount of energy, generated by engines
	 */
	public void generateEnergy(int amount) {
		energyStorage.modifyEnergyStored(amount);
	}
}
