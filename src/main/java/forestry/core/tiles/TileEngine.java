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
package forestry.core.tiles;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.core.IErrorLogic;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.energy.EnergyManager;

import cofh.api.energy.IEnergyConnection;

public abstract class TileEngine extends TileBase implements IEnergyConnection, IActivatable {
	private static final int CANT_SEND_ENERGY_TIME = 20;

	private boolean active = false; // Used for smp.
	private int cantSendEnergyCountdown = CANT_SEND_ENERGY_TIME;
	/**
	 * Indicates whether the piston is receding from or approaching the
	 * combustion chamber
	 */
	private int stagePiston = 0;
	/**
	 * Piston speed as supplied by the server
	 */
	private float pistonSpeedServer = 0;

	protected int currentOutput = 0;
	protected int heat;
	protected final int maxHeat;
	protected boolean forceCooldown = false;
	public float progress;
	protected final EnergyManager energyManager;

	protected TileEngine(String hintKey, int maxHeat, int maxEnergy) {
		super(hintKey);
		this.maxHeat = maxHeat;
		energyManager = new EnergyManager(2000, maxEnergy);

		// allow engines to chain, but not have energy sucked out of them
		energyManager.setReceiveOnly();

		hints.addAll(Config.hints.get("engine"));
	}

	@Override
	public void rotateAfterPlacement(EntityPlayer player, int side) {
		ForgeDirection orientation = ForgeDirection.getOrientation(side).getOpposite();
		if (isOrientedAtEnergyReciever(orientation)) {
			setOrientation(orientation);
		} else {
			super.rotateAfterPlacement(player, side);
			rotate();
		}
	}

	protected void addHeat(int i) {
		heat += i;

		if (heat > maxHeat) {
			heat = maxHeat;
		}
	}

	protected abstract int dissipateHeat();

	protected abstract int generateHeat();

	protected boolean mayBurn() {
		return !forceCooldown;
	}

	protected abstract void burn();

	@Override
	public void updateClientSide() {
		if (stagePiston != 0) {
			progress += pistonSpeedServer;

			if (progress > 1) {
				stagePiston = 0;
				progress = 0;
			}
		} else if (this.active) {
			stagePiston = 1;
		}
	}

	@Override
	protected void updateServerSide() {
		TemperatureState energyState = getTemperatureState();
		if (energyState == TemperatureState.MELTING && heat > 0) {
			forceCooldown = true;
		} else if (forceCooldown && heat <= 0) {
			forceCooldown = false;
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(forceCooldown, EnumErrorCode.FORCED_COOLDOWN);

		boolean enabledRedstone = isRedstoneActivated();
		errorLogic.setCondition(!enabledRedstone, EnumErrorCode.NO_REDSTONE);

		// Determine targeted tile
		TileEntity tile = worldObj.getTileEntity(xCoord + getOrientation().offsetX, yCoord + getOrientation().offsetY, zCoord + getOrientation().offsetZ);

		float newPistonSpeed = getPistonSpeed();
		if (newPistonSpeed != pistonSpeedServer) {
			pistonSpeedServer = newPistonSpeed;
			setNeedsNetworkUpdate();
		}

		if (stagePiston != 0) {

			progress += pistonSpeedServer;

			energyManager.sendEnergy(getOrientation(), tile);

			if (progress > 0.25 && stagePiston == 1) {
				stagePiston = 2;
			} else if (progress >= 0.5) {
				progress = 0;
				stagePiston = 0;
			}
		} else if (enabledRedstone && BlockUtil.isEnergyReceiverOrEngine(getOrientation().getOpposite(), tile)) {
			if (energyManager.canSendEnergy(getOrientation(), tile)) {
				stagePiston = 1; // If we can transfer energy, start running
				setActive(true);
				cantSendEnergyCountdown = CANT_SEND_ENERGY_TIME;
			} else {
				if (isActive()) {
					cantSendEnergyCountdown--;
					if (cantSendEnergyCountdown <= 0) {
						setActive(false);
					}
				}
			}
		} else {
			setActive(false);
		}

		dissipateHeat();
		generateHeat();
		// Now let's fire up the engine:
		if (mayBurn()) {
			burn();
		} else {
			energyManager.drainEnergy(20);
		}
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

		if (!worldObj.isRemote) {
			Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this), worldObj);
		}
	}

	/* INTERACTION */
	@Override
	public boolean rotate(ForgeDirection axis) {
		rotate();

		// it would be irritating if the wrench opened the engine gui when it failed to rotate, so always return true
		return true;
	}

	private void rotate() {
		for (int i = getOrientation().ordinal() + 1; i <= getOrientation().ordinal() + 6; ++i) {
			ForgeDirection orientation = ForgeDirection.values()[i % 6];
			if (isOrientedAtEnergyReciever(orientation)) {
				setOrientation(orientation);
				return;
			}
		}
	}

	private boolean isOrientedAtEnergyReciever(ForgeDirection orientation) {
		TileEntity tile = worldObj.getTileEntity(xCoord + orientation.offsetX, yCoord + orientation.offsetY, zCoord + orientation.offsetZ);
		return BlockUtil.isEnergyReceiverOrEngine(getOrientation().getOpposite(), tile);
	}

	// STATE INFORMATION
	protected double getHeatLevel() {
		return (double) heat / (double) maxHeat;
	}

	protected abstract boolean isBurning();

	public int getBurnTimeRemainingScaled(int i) {
		return 0;
	}

	public boolean hasFuelMin(float percentage) {
		return false;
	}

	public int getCurrentOutput() {
		if (isBurning() && isRedstoneActivated()) {
			return currentOutput;
		} else {
			return 0;
		}
	}

	public int getHeat() {
		return heat;
	}

	/**
	 * Returns the current energy state of the engine
	 */
	public TemperatureState getTemperatureState() {
		return TemperatureState.getState(heat, maxHeat);
	}

	protected float getPistonSpeed() {
		switch (getTemperatureState()) {
			case COOL:
				return 0.03f;
			case WARMED_UP:
				return 0.04f;
			case OPERATING_TEMPERATURE:
				return 0.05f;
			case RUNNING_HOT:
				return 0.06f;
			case OVERHEATING:
				return 0.07f;
			case MELTING:
				return Constants.ENGINE_PISTON_SPEED_MAX;
			default:
				return 0;
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		energyManager.readFromNBT(nbt);

		heat = nbt.getInteger("EngineHeat");

		progress = nbt.getFloat("EngineProgress");
		forceCooldown = nbt.getBoolean("ForceCooldown");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		energyManager.writeToNBT(nbt);

		nbt.setInteger("EngineHeat", heat);
		nbt.setFloat("EngineProgress", progress);
		nbt.setBoolean("ForceCooldown", forceCooldown);
	}

	/* NETWORK */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeBoolean(active);
		data.writeInt(heat);
		data.writeFloat(pistonSpeedServer);
		energyManager.writeData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		active = data.readBoolean();
		heat = data.readInt();
		pistonSpeedServer = data.readFloat();
		energyManager.readData(data);
	}

	/* SMP GUI */
	public abstract void getGUINetworkData(int i, int j);

	public abstract void sendGUINetworkData(Container containerEngine, ICrafting iCrafting);

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return energyManager.canConnectEnergy(from);
	}

	public EnergyManager getEnergyManager() {
		return energyManager;
	}
}
