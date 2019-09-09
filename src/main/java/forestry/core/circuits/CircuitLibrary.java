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
package forestry.core.circuits;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

import forestry.api.circuits.ICircuitLibrary;

public class CircuitLibrary extends WorldSavedData implements ICircuitLibrary {

	public CircuitLibrary(String par1Str) {
		super(par1Str);
	}

	@Override
	public void read(CompoundNBT nbt) {
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		return nbt;
	}

}
