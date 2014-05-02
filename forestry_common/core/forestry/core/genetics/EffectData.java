/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.IEffectData;

public class EffectData implements IEffectData {

	private int[] intData;
	private float[] floatData;
	private boolean[] boolData;

	public EffectData(int intSize, int boolSize) {
		this.intData = new int[intSize];
		this.boolData = new boolean[boolSize];
	}

	@Override
	public void setInteger(int index, int val) {
		intData[index] = val;
	}

	@Override
	public void setBoolean(int index, boolean val) {
		boolData[index] = val;
	}

	@Override
	public int getInteger(int index) {
		return intData[index];
	}

	@Override
	public boolean getBoolean(int index) {
		return boolData[index];
	}

	public int getIntSize() {
		return intData.length;
	}

	@Override
	public void setFloat(int index, float val) {
		floatData[index] = val;
	}

	@Override
	public float getFloat(int index) {
		return floatData[index];
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
	}
}
