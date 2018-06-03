package forestry.energy;

import buildcraft.api.mj.*;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.energy.compat.EnergyStorageWrapper;
import forestry.energy.compat.mj.*;
import forestry.energy.compat.tesla.TeslaConsumerWrapper;
import forestry.energy.compat.tesla.TeslaHelper;
import forestry.energy.compat.tesla.TeslaHolderWrapper;
import forestry.energy.compat.tesla.TeslaProducerWrapper;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.io.IOException;

public class EnergyManager extends EnergyStorage implements IStreamable, INbtReadable, INbtWritable {
	private EnergyTransferMode externalMode = EnergyTransferMode.BOTH;

	public EnergyManager(int maxTransfer, int capacity) {
		super(EnergyHelper.scaleForDifficulty(capacity), EnergyHelper.scaleForDifficulty(maxTransfer), EnergyHelper.scaleForDifficulty(maxTransfer));
	}

	public void setExternalMode(EnergyTransferMode externalMode) {
		this.externalMode = externalMode;
	}

	public EnergyTransferMode getExternalMode() {
		return externalMode;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		final int energy;
		if (nbt.hasKey("EnergyManager")) { // legacy
			NBTTagCompound energyManagerNBT = nbt.getCompoundTag("EnergyManager");
			NBTTagCompound energyStorageNBT = energyManagerNBT.getCompoundTag("EnergyStorage");
			energy = energyStorageNBT.getInteger("Energy");
		} else {
			energy = nbt.getInteger("Energy");
		}

		setEnergyStored(energy);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("Energy", energy);
		return nbt;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeVarInt(this.energy);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
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

	/**
	 * Creates an amount of energy, generated by engines
	 */
	public void generateEnergy(int amount) {
		setEnergyStored(energy + amount);
	}

	public void setEnergyStored(int energyStored) {
		this.energy = energyStored;
		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
	}

	public boolean hasCapability(Capability<?> capability) {
		return capability == CapabilityEnergy.ENERGY ||
				capability == TeslaHelper.TESLA_PRODUCER && externalMode.canExtract() ||
				capability == TeslaHelper.TESLA_CONSUMER && externalMode.canReceive() ||
				capability == TeslaHelper.TESLA_HOLDER ||
				capability == MjHelper.CAP_READABLE ||
				capability == MjHelper.CAP_CONNECTOR ||
				capability == MjHelper.CAP_PASSIVE_PROVIDER && externalMode.canExtract() ||
				capability == MjHelper.CAP_REDSTONE_RECEIVER && externalMode.canReceive() ||
				capability == MjHelper.CAP_RECEIVER && externalMode.canReceive();
	}

	@Nullable
	public <T> T getCapability(Capability<T> capability) {
		if (capability == CapabilityEnergy.ENERGY) {
			IEnergyStorage energyStorage = new EnergyStorageWrapper(this, externalMode);
			return CapabilityEnergy.ENERGY.cast(energyStorage);
		} else if (TeslaHelper.isTeslaCapability(capability)) {
			Capability<ITeslaProducer> teslaProducer = TeslaHelper.TESLA_PRODUCER;
			Capability<ITeslaConsumer> teslaConsumer = TeslaHelper.TESLA_CONSUMER;
			Capability<ITeslaHolder> teslaHolder = TeslaHelper.TESLA_HOLDER;

			if (capability == teslaProducer && externalMode.canExtract()) {
				return teslaProducer.cast(new TeslaProducerWrapper(this));
			} else if (capability == teslaConsumer && externalMode.canReceive()) {
				return teslaConsumer.cast(new TeslaConsumerWrapper(this));
			} else if (capability == teslaHolder) {
				return teslaHolder.cast(new TeslaHolderWrapper(this));
			}
		} else if (MjHelper.isMjCapability(capability)) {
			Capability<IMjConnector> mjConnector = MjHelper.CAP_CONNECTOR;
			Capability<IMjPassiveProvider> mjPassiveProvider = MjHelper.CAP_PASSIVE_PROVIDER;
			Capability<IMjReadable> mjReadable = MjHelper.CAP_READABLE;
			Capability<IMjReceiver> mjReceiver = MjHelper.CAP_RECEIVER;
			Capability<IMjRedstoneReceiver> mjRedstoneReceiver = MjHelper.CAP_REDSTONE_RECEIVER;

			if (capability == mjPassiveProvider && externalMode.canExtract()) {
				return mjPassiveProvider.cast(new MjPassiveProviderWrapper(this));
			} else if (capability == mjReceiver && externalMode.canReceive()) {
				return mjReceiver.cast(new MjReceiverWrapper(this));
			} else if (capability == mjRedstoneReceiver && externalMode.canReceive()) {
				return mjRedstoneReceiver.cast(new MjReceiverWrapper(this));
			} else if (capability == mjReadable) {
				return mjReadable.cast(new MjReadableWrapper(this));
			} else if (capability == mjConnector) {
				return mjConnector.cast(new MjConnectorWrapper(this));
			}
		}
		return null;
	}

	public int calculateRedstone() {
		return MathHelper.floor(((float) energy / (float) capacity) * 14.0F) + (energy > 0 ? 1 : 0);
	}

}
