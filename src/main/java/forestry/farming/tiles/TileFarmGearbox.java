/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IFarmController;
import forestry.energy.EnergyManager;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import net.minecraftforge.common.capabilities.Capability;

public class TileFarmGearbox extends TileFarm implements IEnergyReceiver, IEnergyHandler, IFarmComponent.Active {

	private static final int WORK_CYCLES = 4;
	private static final int ENERGY_PER_OPERATION = WORK_CYCLES * 50;

	private final EnergyManager energyManager;

	private int activationDelay = 0;
	private int previousDelays = 0;
	private int workCounter;

	public TileFarmGearbox() {
		energyManager = new EnergyManager(200, 10000);
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		energyManager.readFromNBT(nbttagcompound);

		activationDelay = nbttagcompound.getInteger("ActivationDelay");
		previousDelays = nbttagcompound.getInteger("PrevDelays");
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		energyManager.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("ActivationDelay", activationDelay);
		nbttagcompound.setInteger("PrevDelays", previousDelays);
		return nbttagcompound;
	}

	@Override
	public void updateServer(int tickCount) {
		if (energyManager.getTotalEnergyStored() <= 0) {
			return;
		}

		if (activationDelay > 0) {
			activationDelay--;
			return;
		}

		// Hard limit to 4 cycles / second.
		if (workCounter < WORK_CYCLES && energyManager.consumeEnergyToDoWork(WORK_CYCLES, ENERGY_PER_OPERATION)) {
			workCounter++;
		}

		if (workCounter >= WORK_CYCLES && tickCount % 5 == 0) {
			IFarmController farmController = getMultiblockLogic().getController();
			if (farmController.doWork()) {
				workCounter = 0;
				previousDelays = 0;
			} else {
				// If the central TE doesn't have work, we add to the activation delay to throttle the CPU usage.
				activationDelay = 10 * previousDelays < 120 ? 10 * previousDelays : 120;
				previousDelays++; // First delay is free!
			}
		}
	}

	@Override
	public void updateClient(int tickCount) {

	}

	/* IEnergyReceiver */
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return energyManager.receiveEnergy(from, maxReceive, simulate);
	}

	public EnergyManager getEnergyManager() {
		return energyManager;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energyManager.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energyManager.getMaxEnergyStored(from);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return energyManager.canConnectEnergy(from);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return energyManager.hasCapability(capability) || super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		T energyCapability = energyManager.getCapability(capability);
		if (energyCapability != null) {
			return energyCapability;
		}
		return super.getCapability(capability, facing);
	}
}
