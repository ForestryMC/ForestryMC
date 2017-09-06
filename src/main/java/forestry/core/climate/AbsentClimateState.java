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
package forestry.core.climate;

import com.google.common.base.MoreObjects;

import javax.annotation.concurrent.Immutable;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.IClimateState;

@Immutable
public final class AbsentClimateState implements IClimateState {

	public static final AbsentClimateState INSTANCE = new AbsentClimateState();

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean(ClimateState.ABSENT_NBT_KEY, true);
		return compound;
	}

	@Override
	public IClimateState toState(ClimateStateType type) {
		return this;
	}

	@Override
	public IClimateState setHumidity(float humidity) {
		return this;
	}

	@Override
	public IClimateState setTemperature(float temperature) {
		return this;
	}

	@Override
	public IClimateState addTemperature(float temperature){
		return this;
	}

	@Override
	public IClimateState addHumidity(float humidity){
		return this;
	}

	@Override
	public IClimateState add(IClimateState state){
		return this;
	}

	@Override
	public IClimateState remove(IClimateState state){
		return this;
	}

	@Override
	public boolean isPresent() {
		return false;
	}

	@Override
	public ClimateStateType getType() {
		return ClimateStateType.IMMUTABLE;
	}

	@Override
	public float getTemperature() {
		return Float.NaN;
	}

	@Override
	public float getHumidity() {
		return Float.NaN;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof IClimateState)){
			return false;
		}
		IClimateState otherState = (IClimateState) obj;
		return otherState.getTemperature() == Float.NaN && otherState.getHumidity() == Float.NaN;
	}

	@Override
	public int hashCode() {
		return Float.hashCode(Float.NaN) * 31 + Float.hashCode(Float.NaN);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("temperature", Float.NaN).add("humidity", Float.NaN).toString();
	}
}
