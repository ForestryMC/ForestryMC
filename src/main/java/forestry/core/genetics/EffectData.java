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
package forestry.core.genetics;

import net.minecraft.nbt.CompoundNBT;

import forestry.api.genetics.IEffectData;

public class EffectData implements IEffectData {

	private final int[] intData;
	private final boolean[] boolData;

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
	public void read(CompoundNBT CompoundNBT) {
	}

	@Override
	public CompoundNBT write(CompoundNBT CompoundNBT) {
		return CompoundNBT;
	}
}
