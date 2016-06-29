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
package forestry.apiculture.multiblock;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import forestry.api.core.climate.IClimateControlled;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;
import forestry.energy.EnergyManager;

import cofh.api.energy.IEnergyReceiver;

public abstract class TileAlvearyClimatiser extends TileAlveary implements IEnergyReceiver, IActivatable, IAlvearyComponent.Climatiser {

	private static final int WORK_CYCLES = 1;
	private static final int ENERGY_PER_OPERATION = 50;

	protected interface IClimitiserDefinition {
		float getChangePerTransfer();

		float getBoundaryUp();

		float getBoundaryDown();
	}

	private final EnergyManager energyManager;
	private final IClimitiserDefinition definition;

	private int workingTime = 0;

	// CLIENT
	private boolean active;

	protected TileAlvearyClimatiser(IClimitiserDefinition definition) {
		this.definition = definition;

		this.energyManager = new EnergyManager(1000, 2000);
		this.energyManager.setReceiveOnly();
	}

	/* UPDATING */
	@Override
	public void changeClimate(int tick, IClimateControlled climateControlled) {
		if (workingTime < 20 && energyManager.consumeEnergyToDoWork(WORK_CYCLES, ENERGY_PER_OPERATION)) {
			// one tick of work for every 10 RF
			workingTime += ENERGY_PER_OPERATION / 10;
		}

		if (workingTime > 0) {
			workingTime--;
			climateControlled.addTemperatureChange(definition.getChangePerTransfer(), definition.getBoundaryDown(), definition.getBoundaryUp());
		}

		setActive(workingTime > 0);
	}

	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		energyManager.readFromNBT(nbttagcompound);
		workingTime = nbttagcompound.getInteger("Heating");
		setActive(workingTime > 0);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		energyManager.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("Heating", workingTime);
		return nbttagcompound;
	}

	/* Network */
	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		packetData.setBoolean("Active", active);
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		setActive(packetData.getBoolean("Active"));
	}

	/* IActivatable */
	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}

		this.active = active;

		if (worldObj != null) {
			if (worldObj.isRemote) {
				worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
			} else {
				Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this), worldObj);
			}
		}
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return energyManager.receiveEnergy(from, maxReceive, simulate);
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

}
