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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.core.IClimateControlled;
import forestry.apiculture.network.PacketActiveUpdate;
import forestry.core.interfaces.IActivatable;
import forestry.core.proxy.Proxies;
import forestry.energy.EnergyManager;

import cofh.api.energy.IEnergyHandler;

public abstract class TileAlvearyClimatiser extends TileAlveary implements IEnergyHandler, IActivatable, IAlvearyComponent.Climatiser {

	protected interface IClimitiserDefinition {
		float getChangePerTransfer();

		float getBoundaryUp();

		float getBoundaryDown();

		int getIconOff();

		int getIconOn();
	}

	private final EnergyManager energyManager;
	private final IClimitiserDefinition definition;

	private int workingTime = 0;

	// CLIENT
	private boolean active;

	public TileAlvearyClimatiser(IClimitiserDefinition definition) {
		this.definition = definition;

		this.energyManager = new EnergyManager(1000, 50, 2000);
		this.energyManager.setReceiveOnly();
	}

	/* UPDATING */
	@Override
	public void changeClimate(int tick, IClimateControlled climateControlled) {
		if (workingTime < 20 && energyManager.consumeEnergyToDoWork()) {
			// consume 10 RF per tick of work
			workingTime += energyManager.getEnergyPerWork() / 10;
		}

		if (workingTime > 0) {
			workingTime--;
			climateControlled.addTemperatureChange(definition.getChangePerTransfer(), definition.getBoundaryDown(), definition.getBoundaryUp());
		}

		setActive(workingTime > 0);
	}

	/* TEXTURES */
	@Override
	public int getIcon(int side, int metadata) {
		if (active) {
			return definition.getIconOn();
		} else {
			return definition.getIconOff();
		}
	}

	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		energyManager.readFromNBT(nbttagcompound);
		workingTime = nbttagcompound.getInteger("Heating");
		setActive(workingTime > 0);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		energyManager.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("Heating", workingTime);
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

		if (worldObj.isRemote) {
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		} else {
			Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this));
		}
	}

	@Override
	public ChunkCoordinates getCoordinates() {
		return new ChunkCoordinates(xCoord, yCoord, zCoord);
	}

	/* IEnergyHandler */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return energyManager.receiveEnergy(from, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return energyManager.extractEnergy(from, maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energyManager.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energyManager.getMaxEnergyStored(from);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return energyManager.canConnectEnergy(from);
	}

}
