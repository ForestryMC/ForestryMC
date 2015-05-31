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
package forestry.apiculture.gadgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.apiculture.network.PacketActiveUpdate;
import forestry.core.interfaces.IActivatable;
import forestry.core.proxy.Proxies;
import forestry.energy.EnergyManager;

import cofh.api.energy.IEnergyHandler;

public abstract class TileAlvearyClimatiser extends TileAlveary implements IEnergyHandler, IActivatable {

	public static class ClimateControl {

		final float changePerTransfer;
		final float boundaryUp;
		final float boundaryDown;

		public ClimateControl(float changePerTransfer, float boundaryDown, float boundaryUp) {
			this.changePerTransfer = changePerTransfer;
			this.boundaryDown = boundaryDown;
			this.boundaryUp = boundaryUp;
		}
	}

	protected final EnergyManager energyManager;
	private final ClimateControl climateControl;
	private int workingTime = 0;
	private final int textureOff;
	private final int textureOn;

	// CLIENT
	private boolean active;

	public TileAlvearyClimatiser(ClimateControl control, int textureOff, int textureOn, int componentBlockMeta) {
		super(componentBlockMeta);
		this.climateControl = control;
		energyManager = new EnergyManager(1000, 50, 2000);
		energyManager.setReceiveOnly();
		this.textureOff = textureOff;
		this.textureOn = textureOn;
	}

	/* UPDATING */
	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (!this.hasMaster()) {
			return;
		}

		if (workingTime < 20 && energyManager.consumeEnergyToDoWork()) {
			// consume 10 RF per tick of work
			workingTime += energyManager.getEnergyPerWork() / 10;
		}

		if (workingTime > 0) {
			workingTime--;
			IAlvearyComponent component = (IAlvearyComponent) this.getCentralTE();
			if (component != null) {
				component.addTemperatureChange(climateControl.changePerTransfer, climateControl.boundaryDown, climateControl.boundaryUp);
			}
		}

		setActive(workingTime > 0);
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* TEXTURES */
	@Override
	public int getIcon(int side, int metadata) {
		if (active) {
			return textureOn;
		} else {
			return textureOff;
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

	/* NETWORK */
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeBoolean(active);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		setActive(data.readBoolean());
	}

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
