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
package forestry.farming.gadgets;

import forestry.api.core.ITileStructure;
import forestry.api.farming.IFarmHousing;
import forestry.core.interfaces.IPowerHandler;
import forestry.energy.EnergyManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileGearbox extends TileFarm implements IPowerHandler {

	public static int WORK_CYCLES = 4;
	private int activationDelay = 0;
	private int previousDelays = 0;
	private int workCounter;
	private final EnergyManager energyManager;

	public TileGearbox() {
		energyManager = new EnergyManager(200, 50, 10000);

		fixedType = TYPE_GEARS;
	}

	@Override
	protected void createInventory() {
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		energyManager.readFromNBT(nbttagcompound);

		activationDelay = nbttagcompound.getInteger("ActivationDelay");
		previousDelays = nbttagcompound.getInteger("PrevDelays");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		energyManager.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("ActivationDelay", activationDelay);
		nbttagcompound.setInteger("PrevDelays", previousDelays);
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();
		
		if (activationDelay > 0) {
			activationDelay--;
			return;
		}

		// Hard limit to 4 cycles / second.
		if (workCounter < WORK_CYCLES && energyManager.consumeEnergyToDoWork()) {
			workCounter++;
		}

		if (workCounter >= WORK_CYCLES && worldObj.getTotalWorldTime() % 5 == 0) {
			ITileStructure central = getCentralTE();
			if (!(central instanceof IFarmHousing))
				return;

			if (((IFarmHousing) central).doWork()) {
				workCounter = 0;
				previousDelays = 0;
			} else {
				// If the central TE doesn't have work, we add to the activation delay to throttle the CPU usage.
				activationDelay = 10 * previousDelays < 120 ? 10 * previousDelays : 120;
				previousDelays++; // First delay is free!
			}
		}
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
