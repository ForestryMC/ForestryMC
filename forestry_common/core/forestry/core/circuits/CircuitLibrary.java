/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.circuits;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

import forestry.api.circuits.ICircuitLibrary;

public class CircuitLibrary extends WorldSavedData implements ICircuitLibrary {

	public CircuitLibrary(String par1Str) {
		super(par1Str);
	}

	@Override
	public void readFromNBT(NBTTagCompound var1) {
	}

	@Override
	public void writeToNBT(NBTTagCompound var1) {
	}

}
