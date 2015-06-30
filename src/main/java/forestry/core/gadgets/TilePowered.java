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

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.core.IErrorLogic;
import forestry.core.EnumErrorCode;
import forestry.core.interfaces.IPowerHandler;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.utils.EnumTankLevel;
import forestry.energy.EnergyManager;

import buildcraft.api.tiles.IHasWork;

@Optional.Interface(iface = "buildcraft.api.tiles.IHasWork", modid = "BuildCraftAPI|tiles")
public abstract class TilePowered extends TileBase implements IRenderableMachine, IPowerHandler, IHasWork {

	public static final int WORK_CYCLES = 4;

	protected final EnergyManager energyManager;

	public TilePowered(int maxTransfer, int energyPerWork, int capacity) {
		this.energyManager = new EnergyManager(maxTransfer, energyPerWork, capacity);
		this.energyManager.setReceiveOnly();
	}

	/* STATE INFORMATION */
	public abstract boolean isWorking();

	public boolean hasResourcesMin(float percentage) {
		return false;
	}

	public boolean hasFuelMin(float percentage) {
		return false;
	}

	public abstract boolean hasWork();

	private int workCounter;

	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		IErrorLogic errorLogic = getErrorLogic();

		boolean disabled = isRedstoneActivated();
		errorLogic.setCondition(disabled, EnumErrorCode.DISABLED);
		if (disabled) {
			return;
		}

		if (updateOnInterval(20)) {
			boolean hasEnergy = energyManager.hasEnergyToDoWork();
			errorLogic.setCondition(!hasEnergy, EnumErrorCode.NOPOWER);
		}

		if (workCounter < WORK_CYCLES) {
			if (energyManager.consumeEnergyToDoWork()) {
				workCounter++;
			}
		} else {
			if (updateOnInterval(5) && workCycle()) {
				workCounter = 0;
			}
		}
	}

	public abstract boolean workCycle();

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
		super.writeGuiData(data);
		energyManager.writeData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		super.readGuiData(data);
		energyManager.readData(data);
	}

	// / ADDITIONAL LIQUID HANDLING
	@Override
	public EnumTankLevel getPrimaryLevel() {
		return EnumTankLevel.EMPTY;
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return EnumTankLevel.EMPTY;
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
