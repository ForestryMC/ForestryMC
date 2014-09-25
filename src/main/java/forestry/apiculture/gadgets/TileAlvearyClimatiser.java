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

import cofh.api.energy.IEnergyHandler;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.core.network.PacketPayload;
import forestry.energy.EnergyManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileAlvearyClimatiser extends TileAlveary implements IEnergyHandler {

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
	protected EnergyManager energyManager;
	ClimateControl climateControl;
	private int transferTime = 0;
	private int animationDelay = 0;
	private final int textureOff;
	private final int textureOn;

	public TileAlvearyClimatiser(ClimateControl control, int textureOff, int textureOn, int componentBlockMeta) {
		super(componentBlockMeta);
		this.climateControl = control;
		energyManager = new EnergyManager(100, 1000, 250, 2000);
		energyManager.setReceiveOnly();
		this.textureOff = textureOff;
		this.textureOn = textureOn;
	}

	@Override
	public void openGui(EntityPlayer player) {
	}

	/* UPDATING */
	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (!this.hasMaster())
			return;

		if (energyManager.consumeEnergyToDoWork()) {

			transferTime = energyManager.getEnergyPerWork();

			if (animationDelay <= 0) {
				animationDelay = 100;
				sendNetworkUpdate();
			} else
				animationDelay = 100;
		}

		if (transferTime > 0) {
			transferTime--;
			IAlvearyComponent component = (IAlvearyComponent) this.getCentralTE();
			if (component != null)
				component.addTemperatureChange(climateControl.changePerTransfer, climateControl.boundaryDown, climateControl.boundaryUp);
		}

		if (animationDelay > 0) {
			animationDelay--;
			if (animationDelay <= 0)
				sendNetworkUpdate();
		}
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* TEXTURES */
	@Override
	public int getIcon(int side, int metadata) {
		if (animationDelay > 0)
			return textureOn;
		else
			return textureOff;
	}

	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		energyManager.readFromNBT(nbttagcompound);
		transferTime = nbttagcompound.getInteger("Heating");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		energyManager.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("Heating", transferTime);
	}


	/* NETWORK */
	@Override
	public void fromPacketPayload(PacketPayload payload) {
		short delay = payload.shortPayload[0];
		if (animationDelay != delay) {
			animationDelay = delay;
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(0, 1);
		payload.shortPayload[0] = (short) animationDelay;
		return payload;
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
