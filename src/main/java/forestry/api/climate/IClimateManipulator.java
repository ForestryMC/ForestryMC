/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * The climate manipulator can be used to manipulate the climate of an {@link IClimateTransformer}.
 */
public interface IClimateManipulator {

	/**
	 * @return The current climate state of this manipulator.
	 */
	IClimateState getCurrent();

	/**
	 * @return The climate state that was set with {@link IClimateManipulatorBuilder#setCurrent(IClimateState)}.
	 */
	IClimateState getStart();

	/**
	 * @return The targeted state of this manipulator and was set with {@link IClimateManipulatorBuilder#setTarget(IClimateState)}.
	 */
	IClimateState getTarget();

	/**
	 * @return The default state of this manipulator and was set with {@link IClimateManipulatorBuilder#setDefault(IClimateState)} (IClimateState)}.
	 */
	IClimateState getDefault();

	/**
	 * @return If the manipulator is allowed to go into the opposite direction. (to negate the change value that was
	 * supplied by the {@link java.util.function.Function} that as set with {@link IClimateManipulatorBuilder#setChangeSupplier(BiFunction)}).
	 */
	boolean allowsBackwards();

	/**
	 * @return The type of the climate that this manipulator can manipulate.
	 */
	ClimateType getType();

	/**
	 * Tries to add the change value to the current state of this transformer. Automatically rounds if the new state would be
	 * near the targeted state.
	 *
	 * @param simulated If the add should only be simulated.
	 * @return The difference between the new current state and the current state before the change was added. Both
	 * values are zero if nothing was / would have been added.
	 */
	IClimateState addChange(boolean simulated);

	/**
	 * Tries to remove the change value from the current state of this transformer. Automatically rounds if the new state would be
	 * near the default state.
	 *
	 * @param simulated If the remove should only be simulated.
	 * @return The difference between the new current state and the current state before the change was removed. Both
	 * values are zero if nothing was / would have been removed.
	 */
	IClimateState removeChange(boolean simulated);

	/**
	 * @return If the change value has the right sign to bring the current climate state closer to the targeted state.
	 */
	boolean canAdd();

	/**
	 * Calls the {@link java.util.function.Consumer} that was set with {@link IClimateManipulatorBuilder#setOnFinish(Consumer)}.
	 */
	void finish();
}
