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
package forestry.greenhouse.tiles;

import net.minecraft.util.EnumFacing;

import forestry.energy.EnergyManager;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;

public class TileGreenhouseGearbox extends TileGreenhouse implements IEnergyReceiver, IEnergyHandler {

	public TileGreenhouseGearbox() {
	}

	/* IEnergyReceiver */
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if(getEnergyManager() == null) {
			return 0;
		}
		return getEnergyManager().receiveEnergy(from, maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		if(getEnergyManager() == null) {
			return 0;
		}
		return getEnergyManager().getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		if(getEnergyManager() == null) {
			return 0;
		}
		return getEnergyManager().getMaxEnergyStored(from);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return getEnergyManager() != null && getEnergyManager().canConnectEnergy(from);
	}
	
	private EnergyManager getEnergyManager() {
		if(!getMultiblockLogic().isConnected()) {
			return null;
		}
		return getMultiblockLogic().getController().getEnergyManager();
	}

}
