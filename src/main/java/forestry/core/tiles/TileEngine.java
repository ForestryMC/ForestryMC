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

import javax.annotation.Nullable;
import java.io.IOException;

import forestry.api.core.IErrorLogic;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.NetworkUtil;
import forestry.energy.EnergyHelper;
import forestry.energy.EnergyManager;
import forestry.energy.EnergyTransferMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEngine extends TileBase implements IActivatable, IStreamableGui {
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
	private final String hintKey;

	protected TileEngine(String hintKey, int maxHeat, int maxEnergy) {
		this.hintKey = hintKey;
		this.maxHeat = maxHeat;
		energyManager = new EnergyManager(2000, maxEnergy);

		energyManager.setExternalMode(EnergyTransferMode.EXTRACT);
	}

	public String getHintKey() {
		return hintKey;
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
		IBlockState blockState = world.getBlockState(getPos());
		EnumFacing facing = blockState.getValue(BlockBase.FACING);
		TileEntity tile = world.getTileEntity(getPos().offset(facing));

		float newPistonSpeed = getPistonSpeed();
		if (newPistonSpeed != pistonSpeedServer) {
			pistonSpeedServer = newPistonSpeed;
			setNeedsNetworkUpdate();
		}

		if (stagePiston != 0) {

			progress += pistonSpeedServer;

			EnergyHelper.sendEnergy(energyManager, facing, tile);

			if (progress > 0.25 && stagePiston == 1) {
				stagePiston = 2;
			} else if (progress >= 0.5) {
				progress = 0;
				stagePiston = 0;
			}
		} else if (enabledRedstone && EnergyHelper.isEnergyReceiverOrEngine(facing.getOpposite(), tile)) {
			if (EnergyHelper.canSendEnergy(energyManager, facing, tile)) {
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

		if (!world.isRemote) {
			NetworkUtil.sendNetworkPacket(new PacketActiveUpdate(this), pos, world);
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		energyManager.writeToNBT(nbt);

		nbt.setInteger("EngineHeat", heat);
		nbt.setFloat("EngineProgress", progress);
		nbt.setBoolean("ForceCooldown", forceCooldown);
		return nbt;
	}

	/* NETWORK */
	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		data.writeBoolean(active);
		data.writeInt(heat);
		data.writeFloat(pistonSpeedServer);
		energyManager.writeData(data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		active = data.readBoolean();
		heat = data.readInt();
		pistonSpeedServer = data.readFloat();
		energyManager.readData(data);
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		data.writeInt(currentOutput);
		data.writeInt(heat);
		data.writeBoolean(forceCooldown);
		energyManager.writeData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		currentOutput = data.readInt();
		heat = data.readInt();
		forceCooldown = data.readBoolean();
		energyManager.readData(data);
	}

	public EnergyManager getEnergyManager() {
		return energyManager;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return energyManager.hasCapability(capability) || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		T energyCapability = energyManager.getCapability(capability);
		if (energyCapability != null) {
			return energyCapability;
		}
		return super.getCapability(capability, facing);
	}
}
