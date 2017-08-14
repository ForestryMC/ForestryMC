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

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;

import forestry.energy.EnergyManager;

public class TileGreenhouseGearbox extends TileGreenhouse {

	public TileGreenhouseGearbox() {
	}

	@Nullable
	public EnergyManager getEnergyManager() {
		if (!getMultiblockLogic().isConnected()) {
			return null;
		}
		return getMultiblockLogic().getController().getEnergyManager();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		EnergyManager energyManager = getEnergyManager();
		if (energyManager != null && energyManager.hasCapability(capability)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}


	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		EnergyManager energyManager = getEnergyManager();
		if (energyManager != null) {
			T energyCapability = energyManager.getCapability(capability);
			if (energyCapability != null) {
				return energyCapability;
			}
		}
		return super.getCapability(capability, facing);
	}
}
