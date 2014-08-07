/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.core.network.PacketPayload;

public abstract class TileAlvearyClimatiser extends TileAlveary implements IPowerReceptor {

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
	PowerHandler powerProvider;
	ClimateControl climateControl;
	private int transferTime = 0;
	private int animationDelay = 0;
	private final int textureOff;
	private final int textureOn;

	public TileAlvearyClimatiser(ClimateControl control, int textureOff, int textureOn, int componentBlockMeta) {
		super(componentBlockMeta);
		this.climateControl = control;
		powerProvider = new PowerHandler(this, Type.MACHINE);
		powerProvider.configure(10, 100, 25, 200);
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

		// BC Power
		if (powerProvider != null) {
			powerProvider.update();
		}

		if (!this.hasMaster())
			return;

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

	/* IPOWERRECEPTOR */
	@Override
	public void doWork(PowerHandler workProvider) {
		if (!this.hasMaster())
			return;

		if (powerProvider.useEnergy(powerProvider.getActivationEnergy(), powerProvider.getEnergyStored(), false) < powerProvider.getActivationEnergy())
			return;

		transferTime = (int) Math.round(powerProvider.useEnergy(powerProvider.getActivationEnergy(), powerProvider.getEnergyStored(), true));

		if (animationDelay <= 0) {
			animationDelay = 100;
			sendNetworkUpdate();
		} else
			animationDelay = 100;
	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return powerProvider.getPowerReceiver();
	}

	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		powerProvider.readFromNBT(nbttagcompound);
		transferTime = nbttagcompound.getInteger("Heating");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		powerProvider.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("Heating", transferTime);
	}

	@Override
	public String getInventoryName() {
		return "tile.alveary.climatiser";
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
}
