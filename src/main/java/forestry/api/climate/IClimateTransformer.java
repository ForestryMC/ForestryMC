/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.function.BiFunction;

import forestry.api.core.ILocatable;

/**
 * A transformer modifies the climate in a specific area.
 * To get the climate at a specific position use {@link IWorldClimateHolder#getClimate(long)}.
 * {@link IClimateTransformer} have to be registered with {@link IWorldClimateHolder#updateTransformer(IClimateTransformer)}
 * and {@link IWorldClimateHolder#removeTransformer(IClimateTransformer)} has to be called after the block that contains
 * the transformer gets harvested / replaced.
 * <p>
 * {@link IWorldClimateHolder#updateTransformer(IClimateTransformer)} has to be called after every change of the climate,
 * the range or the circular state of the transformer.
 */
public interface IClimateTransformer extends ILocatable {
	void addTransformer();

	void removeTransformer();

	/**
	 * @return The parent of this container.
	 */
	IClimateHousing getHousing();

	/**
	 * @return The range of this transformer in one direction from the center of the transformer.
	 */
	int getRange();

	/**
	 * Sets the range of this transformer.
	 * The default range of the value is from 1 to 16.
	 */
	void setRange(int range);

	/**
	 * Sets the circular state of the transformer.
	 */
	void setCircular(boolean circular);

	/**
	 * @return True if the current area of the transformer is circular.
	 */
	boolean isCircular();

	/**
	 * @return The current area of the transformer in blocks.
	 */
	int getArea();

	/**
	 * @return The targeted state of this transformer.
	 */
	IClimateState getTarget();

	/**
	 * Sets the targeted state of this transformer.
	 *
	 * The state automatically gets clamped between 0.0F and 2.0F and transformed into a immutable {@link IClimateState}.
	 */
	void setTarget(IClimateState target);

	/**
	 * @return The immutable current state of the transformer.
	 */
	IClimateState getCurrent();

	/**
	 * Sets the current value of the transformer.
	 *
	 * The state automatically gets clamped between 0.0F and 2.0F and transformed into a immutable {@link IClimateState}.
	 */
	void setCurrent(IClimateState state);

	/**
	 * @return The immutable {@link IClimateState} of the biome in that the transformer is located.
	 */
	IClimateState getDefault();

	/**
	 * @return An object that contains all relevant information about this logic.
	 */
	TransformerProperties createProperties();

	/**
	 * A helper interface that can be usd to manipulate the state of this logic.
	 *
	 * @param type The climate type that can be manipulated with the manipulator.
	 * @param changeSupplier A supplier that supplies the change value that should be applied to the state of this logic.
	 *
	 * @return A helper interface that can be usd to manipulate the state of this logic.
	 */
	IClimateManipulator createManipulator(ClimateType type, BiFunction<ClimateType, TransformerProperties, Float> changeSupplier);
}
