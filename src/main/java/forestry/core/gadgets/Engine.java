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
package forestry.core.gadgets;

import java.io.IOException;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import forestry.api.core.IErrorLogic;
import forestry.apiculture.network.PacketActiveUpdate;
import forestry.core.EnumErrorCode;
import forestry.core.TemperatureState;
import forestry.core.config.Defaults;
import forestry.core.interfaces.IActivatable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.energy.EnergyManager;

import cofh.api.energy.IEnergyConnection;

public abstract class Engine extends TileBase implements IEnergyConnection, IActivatable {

	private boolean active = false; // Used for smp.
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

	protected Engine(int maxHeat, int maxEnergy) {
		this.maxHeat = maxHeat;
		energyManager = new EnergyManager(2000, maxEnergy);

		// allow engines to chain, but not have energy sucked out of them
		energyManager.setReceiveOnly();
	}

	@Override
	public void rotateAfterPlacement(EntityLivingBase entityLiving) {
		rotateEngine();
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
		errorLogic.setCondition(forceCooldown, EnumErrorCode.FORCEDCOOLDOWN);

		boolean enabledRedstone = isRedstoneActivated();
		errorLogic.setCondition(!enabledRedstone, EnumErrorCode.NOREDSTONE);

		// Determine targeted tile
		TileEntity tile = worldObj.getTileEntity(new BlockPos(pos.getX() + getOrientation().getFrontOffsetX(),
				pos.getY() + getOrientation().getFrontOffsetY(), pos.getZ() + getOrientation().getFrontOffsetZ()));

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
			} else {
				setActive(false);
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
	public void rotateEngine() {

		for (int i = getOrientation().ordinal() + 1; i <= getOrientation().ordinal() + 6; ++i) {
			EnumFacing orient = EnumFacing.values()[i % 6];

			TileEntity tile = worldObj.getTileEntity(new BlockPos(pos.getX() + getOrientation().getFrontOffsetX(),
					pos.getY() + getOrientation().getFrontOffsetY(), pos.getZ() + getOrientation().getFrontOffsetZ()));

			if (BlockUtil.isEnergyReceiverOrEngine(getOrientation().getOpposite(), tile)) {
				setOrientation(orient);
				worldObj.notifyBlockOfStateChange(pos, worldObj.getBlockState(pos).getBlock());
				worldObj.markBlockRangeForRenderUpdate(pos, pos);
				break;
			}
		}
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
			return Defaults.ENGINE_PISTON_SPEED_MAX;
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
	public boolean canConnectEnergy(EnumFacing from) {
		return energyManager.canConnectEnergy(from);
	}

	public EnergyManager getEnergyManager() {
		return energyManager;
	}
}
