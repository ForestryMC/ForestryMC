package forestry.api.climate;

import net.minecraft.nbt.NBTTagCompound;

public interface IClimateStates {

	IClimateState create(NBTTagCompound compound);

	IClimateState create(NBTTagCompound compound, ClimateStateType type);

	IClimateState create(float temperature, float humidity, ClimateStateType type);

	default IClimateState create(float temperature, float humidity){
		return create(temperature, humidity, ClimateStateType.MUTABLE);
	}

	IClimateState create(IClimateState climateState, ClimateStateType type);

	default IClimateState create(IClimateState climateState){
		return create(climateState, ClimateStateType.MUTABLE);
	}

	IClimateState absent();

	IClimateState min();

	IClimateState max();
}
