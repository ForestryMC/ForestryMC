package forestry.api.climate;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface IClimateManipulatorBuilder {
	IClimateManipulatorBuilder setType(ClimateType type);

	IClimateManipulatorBuilder setAllowBackwards();

	IClimateManipulatorBuilder setOnFinish(Consumer<IClimateState> onFinish);

	IClimateManipulatorBuilder setChangeSupplier(BiFunction<ClimateType, IClimateManipulator, Float> changeSupplier);

	IClimateManipulatorBuilder setTarget(IClimateState state);

	IClimateManipulatorBuilder setDefault(IClimateState state);

	IClimateManipulatorBuilder setCurrent(IClimateState state);

	IClimateManipulator build();
}
