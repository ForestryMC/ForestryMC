/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.gadgets;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

import forestry.api.core.ITileStructure;
import forestry.api.farming.IFarmHousing;
import forestry.core.GameMode;
import forestry.core.interfaces.IPowerHandler;

public class TileGearbox extends TileFarm implements IPowerHandler {

	public static int WORK_CYCLES = 4;
	public static int MIN_ENERGY_RECEIVED = 5;
	public static int MAX_ENERGY_RECEIVED = 20;
	public static int MIN_ACTIVATION_ENERGY = 5;
	public static int MAX_ENERGY = 1000;
	private int activationDelay = 0;
	private int previousDelays = 0;
	private int workCounter;

	public TileGearbox() {
		powerProvider = new PowerHandler(this, Type.MACHINE);
		powerProvider.configurePowerPerdition(0, 100);
		powerProvider.configure(MIN_ENERGY_RECEIVED,
				Math.round(MAX_ENERGY_RECEIVED * GameMode.getGameMode().getFloatSetting("energy.demand.modifier")),
				Math.round(MIN_ACTIVATION_ENERGY * GameMode.getGameMode().getFloatSetting("energy.demand.modifier")),
				Math.round(MAX_ENERGY * GameMode.getGameMode().getFloatSetting("energy.demand.modifier")));

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
		powerProvider.readFromNBT(nbttagcompound);

		activationDelay = nbttagcompound.getInteger("ActivationDelay");
		previousDelays = nbttagcompound.getInteger("PrevDelays");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		powerProvider.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("ActivationDelay", activationDelay);
		nbttagcompound.setInteger("PrevDelays", previousDelays);
	}

	/* IPOWERRECEPTOR */
	PowerHandler powerProvider;

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return powerProvider.getPowerReceiver();
	}

	@Override
	public PowerHandler getPowerHandler() {
		return powerProvider;
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();
		
		if (activationDelay > 0) {
			activationDelay--;
			return;
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

	@Override
	public void doWork(PowerHandler workProvider) {
		// Hard limit to 4 cycles / second.
		if (workCounter < WORK_CYCLES) {
			powerProvider.useEnergy(MIN_ACTIVATION_ENERGY, MIN_ACTIVATION_ENERGY, true);
			workCounter++;
		}
	}
}
