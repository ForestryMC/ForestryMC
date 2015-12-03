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

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.core.IErrorLogic;
import forestry.core.circuits.ISpeedUpgradable;
import forestry.core.config.Config;
import forestry.core.errors.EnumErrorCode;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.render.TankRenderInfo;
import forestry.energy.EnergyManager;

import buildcraft.api.tiles.IHasWork;

@Optional.Interface(iface = "buildcraft.api.tiles.IHasWork", modid = "BuildCraftAPI|tiles")
public abstract class TilePowered extends TileBase implements IRenderableTile, IPowerHandler, IHasWork, ISpeedUpgradable, IStreamableGui {
	private static final int WORK_TICK_INTERVAL = 5; // one Forestry work tick happens every WORK_TICK_INTERVAL game ticks

	private final EnergyManager energyManager;

	private int workCounter;
	private int ticksPerWorkCycle;
	private int energyPerWorkCycle;

	protected float speedMultiplier = 1.0f;
	protected float powerMultiplier = 1.0f;

	// the number of work ticks that this tile has had no power
	private int noPowerTime = 0;

	protected TilePowered(String hintKey, int maxTransfer, int capacity) {
		super(hintKey);
		this.energyManager = new EnergyManager(maxTransfer, capacity);
		this.energyManager.setReceiveOnly();

		this.ticksPerWorkCycle = 4;

		hints.addAll(Config.hints.get("powered.machine"));
	}

	public int getWorkCounter() {
		return workCounter;
	}

	public void setTicksPerWorkCycle(int ticksPerWorkCycle) {
		this.ticksPerWorkCycle = ticksPerWorkCycle;
		this.workCounter = 0;
	}

	public int getTicksPerWorkCycle() {
		if (worldObj.isRemote) {
			return ticksPerWorkCycle;
		}
		return Math.round(ticksPerWorkCycle / speedMultiplier);
	}

	public void setEnergyPerWorkCycle(int energyPerWorkCycle) {
		this.energyPerWorkCycle = EnergyManager.scaleForDifficulty(energyPerWorkCycle);
	}

	public int getEnergyPerWorkCycle() {
		return Math.round(energyPerWorkCycle * powerMultiplier);
	}

	/* STATE INFORMATION */
	public boolean hasResourcesMin(float percentage) {
		return false;
	}

	public boolean hasFuelMin(float percentage) {
		return false;
	}

	public abstract boolean hasWork();

	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (!updateOnInterval(WORK_TICK_INTERVAL)) {
			return;
		}

		IErrorLogic errorLogic = getErrorLogic();

		boolean disabled = isRedstoneActivated();
		errorLogic.setCondition(disabled, EnumErrorCode.DISABLED_BY_REDSTONE);
		if (disabled) {
			return;
		}

		if (!hasWork()) {
			return;
		}

		int ticksPerWorkCycle = getTicksPerWorkCycle();

		if (workCounter < ticksPerWorkCycle) {
			int energyPerWorkCycle = getEnergyPerWorkCycle();
			boolean consumedEnergy = energyManager.consumeEnergyToDoWork(ticksPerWorkCycle, energyPerWorkCycle);
			if (consumedEnergy) {
				errorLogic.setCondition(false, EnumErrorCode.NO_POWER);
				workCounter++;
				noPowerTime = 0;
			} else {
				noPowerTime++;
				if (noPowerTime > 4) {
					errorLogic.setCondition(true, EnumErrorCode.NO_POWER);
				}
			}
		}

		if (workCounter >= ticksPerWorkCycle) {
			if (workCycle()) {
				workCounter = 0;
			}
		}
	}

	protected abstract boolean workCycle();

	public int getProgressScaled(int i) {
		int ticksPerWorkCycle = getTicksPerWorkCycle();
		if (ticksPerWorkCycle == 0) {
			return 0;
		}

		return (workCounter * i) / ticksPerWorkCycle;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		energyManager.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		energyManager.readFromNBT(nbt);
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		energyManager.writeData(data);
		data.writeVarInt(workCounter);
		data.writeVarInt(getTicksPerWorkCycle());
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		energyManager.readData(data);
		workCounter = data.readVarInt();
		ticksPerWorkCycle = data.readVarInt();
	}

	/* ISpeedUpgradable */
	@Override
	public void applySpeedUpgrade(double speedChange, double powerChange) {
		speedMultiplier += speedChange;
		powerMultiplier += powerChange;
		workCounter = 0;
	}

	/* IRenderableTile */
	@Override
	public TankRenderInfo getResourceTankInfo() {
		return TankRenderInfo.EMPTY;
	}

	@Override
	public TankRenderInfo getProductTankInfo() {
		return TankRenderInfo.EMPTY;
	}

	/* IPowerHandler */
	@Override
	public EnergyManager getEnergyManager() {
		return energyManager;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return energyManager.receiveEnergy(from, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return energyManager.extractEnergy(from, maxReceive, simulate);
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
