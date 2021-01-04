package forestry.api.climate;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * The builder can be used to create a {@link IClimateManipulator}
 */
public interface IClimateManipulatorBuilder {
	/**
	 * Sets the type of the value that the manipulator should manipulate to the given type.
	 */
	IClimateManipulatorBuilder setType(ClimateType type);

	/**
	 * Allows the manipulator to move backwards.(to negate the change value that was supplied by the
	 * {@link java.util.function.Function} that as set with {@link IClimateManipulatorBuilder#setChangeSupplier(BiFunction)}).
	 */
	IClimateManipulatorBuilder setAllowBackwards();

	/**
	 * Sets the consumer that gets called at {@link IClimateManipulator#finish()}.
	 */
	IClimateManipulatorBuilder setOnFinish(Consumer<IClimateState> onFinish);

	/**
	 * Sets the {@link BiFunction} that supplies the change value of the manipulator.
	 */
	IClimateManipulatorBuilder setChangeSupplier(BiFunction<ClimateType, IClimateManipulator, Float> changeSupplier);

	/**
	 * Sets the targeted state of this manipulator.
	 */
	IClimateManipulatorBuilder setTarget(IClimateState state);

	/**
	 * Sets the default state of this manipulator. (The state of the manipulator without any change)
	 */
	IClimateManipulatorBuilder setDefault(IClimateState state);

	/**
	 * Sets the state at that the manipulator starts to manipulate.
	 */
	IClimateManipulatorBuilder setCurrent(IClimateState state);

	/**
	 * Creates the manipulator based on the data that was supplied to this builder.
	 */
	IClimateManipulator build();
}
