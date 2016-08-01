package forestry.energy;

import java.io.IOException;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import forestry.api.core.ForestryAPI;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.tiles.TileEngine;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
		@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "Tesla"),
		@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "Tesla"),
		@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "Tesla")
})
public class EnergyManager implements IEnergyReceiver, ITeslaConsumer, ITeslaProducer, ITeslaHolder, IEnergyProvider, IStreamable {
	@CapabilityInject(ITeslaConsumer.class)
	public static Capability<ITeslaConsumer> TESLA_CONSUMER = null;
	@CapabilityInject(ITeslaProducer.class)
	public static Capability<ITeslaProducer> TESLA_PRODUCER = null;
	@CapabilityInject(ITeslaHolder.class)
	public static Capability<ITeslaHolder> TESLA_HOLDER = null;

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

	@SuppressWarnings("incomplete-switch")
	private boolean canExtract() {
		switch (mode) {
			case EXTRACT:
			case BOTH:
				return true;
		}
		return false;
	}

	@SuppressWarnings("incomplete-switch")
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
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (!canReceive()) {
			return 0;
		}
		return energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}
		return energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energyStorage.getEnergyStored();
	}

	public int getTotalEnergyStored() {
		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energyStorage.getMaxEnergyStored();
	}

	public int getMaxEnergyStored() {
		return energyStorage.getMaxEnergyStored();
	}

	public int getMaxEnergyReceived() {
		return energyStorage.getMaxReceive();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
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
	public boolean canSendEnergy(EnumFacing orientation, TileEntity tile) {
		return sendEnergy(orientation, tile, Integer.MAX_VALUE, true) > 0;
	}

	/**
	 * Sends as much energy as it can to the tile at orientation.
	 * For power sources. Ignores canExtract()
	 *
	 * @return amount sent
	 */
	public int sendEnergy(EnumFacing orientation, TileEntity tile) {
		return sendEnergy(orientation, tile, Integer.MAX_VALUE, false);
	}

	/**
	 * Sends amount of energy to the tile at orientation.
	 * For power sources. Ignores canExtract()
	 *
	 * @return amount sent
	 */
	public int sendEnergy(EnumFacing orientation, TileEntity tile, int amount, boolean simulate) {
		int sent = 0;
		if (tile != null) {
			int extractable = energyStorage.extractEnergy(amount, true);
			if (extractable > 0) {
				EnumFacing side = orientation.getOpposite();
				if (TESLA_CONSUMER != null && tile.hasCapability(TESLA_CONSUMER, side)) {
					sent = sendEnergyTesla(tile, side, extractable, simulate);
				} else if (tile instanceof IEnergyReceiver) {
					IEnergyReceiver receptor = (IEnergyReceiver) tile;
					sent = receptor.receiveEnergy(side, extractable, simulate);
				} else if (tile instanceof TileEngine) { // engine chaining
					TileEngine receptor = (TileEngine) tile;
					sent = receptor.getEnergyManager().receiveEnergy(side, extractable, simulate);
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

	public boolean hasCapability(Capability<?> capability) {
		if (capability == TESLA_PRODUCER && canExtract()) {
			return true;
		} else if (capability == TESLA_CONSUMER && canReceive()) {
			return true;
		} else if (capability == TESLA_HOLDER) {
			return true;
		}
		return false;
	}

	public <T> T getCapability(Capability<T> capability) {
		if (capability == TESLA_PRODUCER && canExtract()) {
			return TESLA_PRODUCER.cast(this);
		} else if (capability == TESLA_CONSUMER && canReceive()) {
			return TESLA_CONSUMER.cast(this);
		} else if (capability == TESLA_HOLDER) {
			return TESLA_HOLDER.cast(this);
		}
		return null;
	}

	@Optional.Method(modid = "Tesla")
	public static int sendEnergyTesla(TileEntity tile, EnumFacing side, int amount, boolean simulate) {
		ITeslaConsumer consumer = tile.getCapability(TESLA_CONSUMER, side);
		return (int) consumer.givePower(amount, simulate);
	}

	@Optional.Method(modid = "Tesla")
	@Override
	public long givePower(long power, boolean simulated) {
		if (!canReceive()) {
			return 0;
		}
		return energyStorage.receiveEnergy(power, simulated);
	}

	@Optional.Method(modid = "Tesla")
	@Override
	public long takePower(long power, boolean simulated) {
		if (!canExtract()) {
			return 0;
		}
		return energyStorage.extractEnergy(power, simulated);
	}

	@Optional.Method(modid = "Tesla")
	@Override
	public long getStoredPower() {
		return energyStorage.getEnergyStored();
	}

	@Optional.Method(modid = "Tesla")
	@Override
	public long getCapacity() {
		return energyStorage.getMaxEnergyStored();
	}
}
