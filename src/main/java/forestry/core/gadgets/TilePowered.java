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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.Optional;

import forestry.core.interfaces.IPowerHandler;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.proxy.Proxies;
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
	public void update() {
		super.update();
		if (!Proxies.common.isSimulating(worldObj)) {
			return;
		}

		// Disable powered machines on a direct redstone signal
		if (worldObj.getStrongPower(pos) >= 15) {
			return;
		}

		if (workCounter < WORK_CYCLES && energyManager.consumeEnergyToDoWork()) {
			workCounter++;
		}

		if (workCounter >= WORK_CYCLES && updateOnInterval(5)) {
			if (workCycle()) {
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
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return energyManager.receiveEnergy(from, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return energyManager.extractEnergy(from, maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energyManager.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energyManager.getMaxEnergyStored(from);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return energyManager.canConnectEnergy(from);
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
