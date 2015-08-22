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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import sun.awt.windows.WPrinterJob;
import forestry.core.TemperatureState;
import forestry.core.config.Defaults;
import forestry.core.network.PacketPayload;
import forestry.core.utils.BlockUtil;
import forestry.energy.EnergyManager;

import cofh.api.energy.IEnergyConnection;

public abstract class Engine extends TileBase implements IEnergyConnection {

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(3, 1, 0);

		if (this.isActive) {
			payload.intPayload[0] = 1;
		} else {
			payload.intPayload[0] = 0;
		}
		payload.intPayload[1] = energyManager.toPacketInt();
		payload.intPayload[2] = heat;

		payload.floatPayload[0] = pistonSpeedServer;
		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {

		isActive = payload.intPayload[0] > 0;
		energyManager.fromPacketInt(payload.intPayload[1]);
		heat = payload.intPayload[2];

		pistonSpeedServer = payload.floatPayload[0];
	}

	public boolean isActive = false; // Used for smp.
	/**
	 * Indicates whether the piston is receding from or approaching the
	 * combustion chamber
	 */
	public int stagePiston = 0;
	/**
	 * Piston speed as supplied by the server
	 */
	public float pistonSpeedServer = 0;
	protected int currentOutput = 0;
	public final int maxEnergy;
	public final int maxEnergyExtracted;
	public int heat;
	protected final int maxHeat;
	protected boolean forceCooldown = false;
	public float progress;
	protected final EnergyManager energyManager;

	public Engine(int maxHeat, int maxEnergy, int maxEnergyExtracted) {
		this.maxHeat = maxHeat;
		this.maxEnergy = maxEnergy;
		this.maxEnergyExtracted = maxEnergyExtracted;
		energyManager = new EnergyManager(2000, 100, 1000000);

		// allow engines to chain, but not have energy sucked out of them
		energyManager.setReceiveOnly();
	}

	@Override
	public void rotateAfterPlacement(World world, BlockPos pos, EntityLivingBase entityliving, ItemStack itemstack) {
		rotateEngine();
	}

	protected void addHeat(int i) {
		heat += i;

		if (heat > maxHeat) {
			heat = maxHeat;
		}
	}

	public abstract int dissipateHeat();

	public abstract int generateHeat();

	public boolean mayBurn() {
		return !forceCooldown;
	}

	public abstract void burn();

	@Override
	public void updateClientSide() {
		if (stagePiston != 0) {
			progress += pistonSpeedServer;

			if (progress > 1) {
				stagePiston = 0;
				progress = 0;
			}
		} else if (this.isActive) {
			stagePiston = 1;
		}
	}

	@Override
	public void updateServerSide() {
		TemperatureState energyState = getTemperatureState();
		if (energyState == TemperatureState.MELTING && heat > 0) {
			forceCooldown = true;
		} else if (forceCooldown && heat <= 0) {
			forceCooldown = false;
		}

		// Determine targeted tile
		TileEntity tile = worldObj.getTileEntity(new BlockPos(pos.getX() + getOrientation().getFrontOffsetX(), pos.getY() + getOrientation().getFrontOffsetY(), pos.getZ() + getOrientation().getFrontOffsetZ()));

		float newPistonSpeed = getPistonSpeed();
		if (newPistonSpeed != pistonSpeedServer) {
			pistonSpeedServer = newPistonSpeed;
			sendNetworkUpdate();
		}

		if (stagePiston != 0) {

			progress += pistonSpeedServer;

			if (progress > 0.25 && stagePiston == 1) {
				stagePiston = 2;

				energyManager.sendEnergy(getOrientation(), tile);

			} else if (progress >= 0.5) {
				progress = 0;
				stagePiston = 0;
			}

		} else if (canPowerTo(tile)) // If we are not already running, check if
		{
			if (energyManager.getEnergyStored(getOrientation()) > 0) {
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

	private boolean canPowerTo(TileEntity tile) {
		return isActivated() && BlockUtil.isEnergyReceiver(getOrientation().getOpposite(), tile);
	}

	private void setActive(boolean isActive) {
		if (this.isActive == isActive) {
			return;
		}

		this.isActive = isActive;
		sendNetworkUpdate();
	}

	/* INTERACTION */
	public void rotateEngine() {

		for (int i = getOrientation().ordinal() + 1; i <= getOrientation().ordinal() + 6; ++i) {
			EnumFacing orient = EnumFacing.values()[i % 5];

			TileEntity tile = worldObj.getTileEntity(new BlockPos(pos.getX() + orient.getFrontOffsetX(), pos.getY() + orient.getFrontOffsetY(), pos.getZ() + orient.getFrontOffsetZ()));

			if (BlockUtil.isEnergyReceiver(getOrientation().getOpposite(), tile)) {
				setOrientation(orient);
				worldObj.notifyNeighborsOfStateChange(pos, worldObj.getBlockState(pos).getBlock());
				worldObj.func_147479_m(pos);
				break;
			}
		}
	}

	// STATE INFORMATION
	protected double getHeatLevel() {
		return (double) heat / (double) maxHeat;
	}

	public abstract boolean isBurning();

	public int getBurnTimeRemainingScaled(int i) {
		return 0;
	}

	public boolean hasFuelMin(float percentage) {
		return false;
	}

	public int getCurrentOutput() {
		if (isBurning() && isActivated()) {
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
		// double scaledStorage = (double)storedEnergy / (double)maxEnergy;
		double scaledHeat = (double) heat / (double) maxHeat;

		if (scaledHeat < 0.20) {
			return TemperatureState.COOL;
		} else if (scaledHeat < 0.45) {
			return TemperatureState.WARMED_UP;
		} else if (scaledHeat < 0.65) {
			return TemperatureState.OPERATING_TEMPERATURE;
		} else if (scaledHeat < 0.85) {
			return TemperatureState.RUNNING_HOT;
		} else if (scaledHeat < 1.0) {
			return TemperatureState.OVERHEATING;
		} else {
			return TemperatureState.MELTING;
		}
	}

	public float getPistonSpeed() {
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
	
	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}
}
