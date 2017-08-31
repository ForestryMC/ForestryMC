package forestry.core.climate;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateStates;

public class ClimateStates implements IClimateStates {

	@Override
	public IClimateState create(IClimateState climateState, ClimateStateType type) {
		return new ClimateState(climateState, type);
	}

	@Override
	public IClimateState create(float temperature, float humidity, ClimateStateType type) {
		return new ClimateState(temperature, humidity, type);
	}

	@Override
	public IClimateState create(NBTTagCompound compound, ClimateStateType type) {
		if(compound.getBoolean(ClimateState.ABSENT_NBT_KEY)){
			return AbsentClimateState.INSTANCE;
		}
		return new ClimateState(compound, type);
	}

	@Override
	public IClimateState create(NBTTagCompound compound) {
		if(compound.getBoolean(ClimateState.ABSENT_NBT_KEY)){
			return AbsentClimateState.INSTANCE;
		}
		return new ClimateState(compound);
	}

	@Override
	public IClimateState absent() {
		return AbsentClimateState.INSTANCE;
	}

	@Override
	public IClimateState min() {
		return ClimateState.MIN;
	}

	@Override
	public IClimateState max() {
		return ClimateState.MAX;
	}
}
